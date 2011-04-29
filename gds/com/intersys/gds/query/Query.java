package com.intersys.gds.query;

import java.util.HashSet;
import java.util.Iterator;

import com.intersys.gds.Document;
import com.intersys.gds.DocumentMap;
import com.intersys.gds.ElementType;
import com.intersys.gds.Util;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

/** <CODE>Query</CODE> is a base class for all ad-hoc GDS query
 * types. It is an abstract class allowing each query subtype to
 * provide it's own execute method. <CODE>Query</CODE> implements
 * next method, which returns the next Document returned by the
 * underlying query. It is important to state that the data is not
 * materialized on the Java side until next is called. Query
 * execution merely generates a set of candidate ids, which are
 * then used in <CODE>next</CODE> to return the actual Documents.
 * Total number of Documents, after the query has been executed,
 * but before a first call to next is made, is available by calling
 * <CODE>size</CODE>. Number of remaining Documents *after* at least
 * one call to <CODE>next</CODE> is available by calling
 * <CODE>itemsLeft</CODE>. Number of Documents a query can return can
 * be limited by using <CODE>limit</CODE>
 *
 * Important: For now only index based queries are supported, meaning
 * data has to have been previously indexed in order for queries to
 * work. Should an attempt be made to run a query and no index is present
 * an exception is thrown.
 *
 */

public abstract class Query {

    HashSet<String>     candidates =  null;
    boolean             beforeFirst = true;
    Object              scalar = null;
    String              key = null;
    DocumentMap         documentMap;
    NodeReference       indexGlobal;
    String              candidate = "";
    int                 valueType = -1;
    int                 limit = 0;
    Iterator<String>    candidateIt;
    boolean             uniqueIndex = false;

    Query() { }
  
    /** Abstract apply method. Each query will provide it's
     * own implementation.
     *
     * @param candidate candidate
     *
     */
    public abstract boolean apply(Object candidate);

    /** Returns the next Document matching this Query. Returns null
     * if no more Documents.
     *
     * @return Document next Document or null if none
     *
     */
    public Document next() {
        if (!candidateIt.hasNext()) {
            return null;
        }
        //if ((candidates == null) || (candidates.isEmpty())) {
        //    return null;
        //}
        Document document = documentMap.load(candidateIt.next().toString());
        //candidateIt.remove();
        beforeFirst = false;
        return document;
    }

    /** Returns the size of the result set. Can be called only
     * before the first call to next, otherwise an exception is
     * thrown. 
     *
     * @return int size of the result set
     *
     */
    public int size() {
        if (!beforeFirst) {
            throw new RuntimeException("next already called; to get remaining item count use itemsLeft");
        }
        if ((candidates == null) || (candidates.isEmpty())) {
            return 0;
        }
        return candidates.size();
    }

    /** Limit the size of the result set. Defaults to 0 (zero)
     * which means there will be no limit set.
     *
     * @param lim result set size limit
     *
     */
    public void limit(int lim) {
        limit = lim;
    }

    /** Returns the number of remaining items in the result set.
     *
     * @return int number of remaining items in the result set
     *
     */
    public int itemsLeft() {
        if ((candidates == null) || (candidates.isEmpty())) {
            return 0;
        }
        return candidates.size();
    }

    /** execute method. Executes a query by generating a list of
     * candidates. Each candidate is fed to the appropriate apply
     * method. That method determines whether a candidate satisfies
     * the query condition. If so, it is added to the list of
     * candidates later to be retrieved by calling next
     *
     * @param dm Document Map
     */
    public void execute(DocumentMap dm) {
        init(dm);
        if (key != null) {
            valueType = documentMap.getDocumentType().getType(key);
        }
        while (true) {
            if ((limit != 0) && (candidates.size() == limit)) {
                return;
            }
            candidate = indexGlobal.nextSubscript(candidate);
            if (candidate.equals("")) {
                break;
            }
            if (!apply(ElementType.cast(candidate,valueType))) {
                continue;
            }
            // multiple index values
            if (uniqueIndex) { //(indexGlobal.hasSubnodes(candidate)) {
                candidates.add(indexGlobal.getString(candidate));
            } else {
                ValueList indexList = indexGlobal.getList(candidate);
                for (int i=0;i<indexList.length();i++) {
                    candidates.add(indexList.getNextString());
                }
            }
        }
        candidateIt = candidates.iterator();
    }

    void init(DocumentMap dm) {
        documentMap = dm;
        uniqueIndex = documentMap.getDocumentType().isIndexUnique(key);
        candidates = new HashSet<String>();
        indexGlobal = documentMap.getConnection().createNodeReference(dm.getName()+"I");
        indexGlobal.appendSubscript(key);
        if (!indexGlobal.hasSubnodes()) {
            throw new RuntimeException("Currently only indexed queries are supported");
        }
    }
}
