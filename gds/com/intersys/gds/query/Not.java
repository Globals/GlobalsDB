package com.intersys.gds.query;

import com.intersys.gds.DocumentMap;
import com.intersys.globals.ValueList;

/** <CODE>Not</CODE> query. Executes a negation of a sub-query.
 *
 */
public class Not extends Query {

    Query   query;

   /** <CODE>Not</CODE> query constructor. Takes a query as a parameter.
    *
    */
    public Not(Query q) {
        query = q;
    }

    /** execute method. Executes a query by generating a list of
     * candidates. Each candidate is fed to the appropriate apply
     * method. That method determines whether a candidate satisfies
     * the query condition. If so, it is added to the list of
     * candidates later to be retrieved by calling next
     *
     * @param dm Document Map
     *
     */
    public void execute(DocumentMap dm) {
        init(dm);
        while (true) {
            if ((limit != 0) && (candidates.size() == limit)) {
                return;
            }
            candidate = indexGlobal.nextSubscript(candidate);
            if (candidate.equals("")) {
                break;
            }
            if (query.apply(candidate)) {
                continue;
            }

            if (uniqueIndex) {
                candidates.add(indexGlobal.getString(candidate));
            } else {
                ValueList indexList = indexGlobal.getList(candidate);
                for (int i=0;i<indexList.length();i++) {
                    candidates.add(indexList.getNextString());
                }
            }
        }
    }

    public boolean apply(Object candidate) {
        throw new UnsupportedOperationException("Not applicable for logical expressions.");
    }

}
