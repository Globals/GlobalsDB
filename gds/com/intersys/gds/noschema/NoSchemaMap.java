package com.intersys.gds.noschema;

import com.intersys.gds.Util;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

/** <CODE>NoSchemaMap</CODE> is a No Schema version of DocumentMap.
 * In the no schema version of GDS, individual NoSchemaDocument keys are stored
 * into the database along with the corresponding values. 
 *
 */

public class NoSchemaMap {

    private NodeReference           global;
    private String                  name;
    private ValueList               valueList;
    private NoSchemaConnection      connection;
    private ValueList               scratchList;

    public NoSchemaMap(String n, NoSchemaConnection conn) {
        connection = conn;
        name = n;
        global = connection.createNodeReference(name);
        valueList = connection.createList();
        scratchList = connection.createList();
    }

    /** Stores a NoSchemaDocument. If store succeeds, the NoSchemaDocument is also
     * removed from the runtime memory.
     *
     * @param key key
     * @param value NoSchemaDocument to store
     *
     */
     public void store(String key, NoSchemaDocument value) {
        valueList.clear();
        global.appendSubscript(key);
        write(value,valueList);
        value.setDBID(name,key);
        global.set(valueList);
        global.setSubscriptCount(0);
    }

    /** Loads a NoSchemaDocument given it's key. Since there is no schema
     * an instance of a NoSchemaDocument needs to be passed in. Returns the
     * corresponding NoSchemaDocument, or null if there isn't one. Since there
     * is no schema, load method requires a fully initialized sample object so
     * that it properly fill in the data as loaded from the database.
     *
     * @param key key
     * @param sampleDocument an instance of NoSchemaDocument to be filled in
     *
     * @return NoSchemaDocument NoSchemaDocument
     */
    public NoSchemaDocument load(String key, NoSchemaDocument sampleDocument) {
        if (sampleDocument == null) {
            throw new RuntimeException("Cannot reconstruct object, no schema supplied");
        }
        global.setSubscriptCount(0);
        global.appendSubscript(key);
        valueList = global.getList(valueList);
        NoSchemaDocument document = new NoSchemaDocument();
        read(document,valueList,sampleDocument);
        document.setDBID(name,key);
        return document;
    }

    private void write(NoSchemaDocument document, ValueList valueList) {
        for (String key : document.keySet()) {
            Object val = document.get(key);
            valueList.append(key);
            if (val instanceof NoSchemaDocument) {
                NoSchemaDocument doc = (NoSchemaDocument) val;
                writeDocument(doc,valueList,scratchList,(doc.getDBID() != null));
            } else if (val instanceof NoSchemaDocument[]) {
                scratchList.clear();
                ValueList tempList = connection.createList();
                for (int j=0;j<((NoSchemaDocument[])val).length;j++) {
                    NoSchemaDocument doc = ((NoSchemaDocument[])val)[j];
                    writeDocument(doc,scratchList,tempList,(document.getDBID() != null));
                }
                valueList.append(scratchList);
           } else {
                valueList.append(val);
            }
        }
    }

    private void writeDocument(NoSchemaDocument doc, ValueList valueList, ValueList scratch, boolean isReference) {
        if (isReference) {
            ValueList tList = connection.createList();
            tList.append(doc.getName());
            tList.append(doc.getDBID());
            valueList.append(tList);
            return;
        }
        scratch.clear();
        write(doc,scratch);
        valueList.append(scratch);
    }

    private void read(NoSchemaDocument document, ValueList valueList, NoSchemaDocument blueprint) {
        for (int i=0;i<valueList.length()/2;i++) {
            String key = valueList.getNextString();
            Class type = blueprint.get(key).getClass();
            if (Util.isDatatype(type)) {
                document.put(key,valueList.getNextObject());
            } else if (type.isArray()) {
                // DocumentType.TYPE_EMBEDDED_ARRAY
                ValueList tempList = valueList.getNextList();
                if ((tempList == null) || (tempList.length() == 0)) {
                    document.put(key,null);
                    continue;
                }
                NoSchemaDocument[] references = new NoSchemaDocument[tempList.length()];
                for (int j=0;j<tempList.length();j++) {
                    references[j] = new NoSchemaDocument();
                    ValueList tList = tempList.getNextList();
                    read(references[j],tList,((NoSchemaDocument[])blueprint.get(key))[0]);
                }
                document.put(key,references);
            //} TODO else if (type == DocumentType.TYPE_REFERENCE_ARRAY) {
            // embedded or reference
            } else {
                Object nestedDoc = blueprint.get(key);
                // embedded
                if (nestedDoc instanceof NoSchemaDocument) {
                    ValueList vList = valueList.getNextList();
                    NoSchemaDocument nDocument = new NoSchemaDocument();
                    for (int j=0;j<vList.length()/2;j++) {
                        String nKey = vList.getNextString();
                        // TODO more nesting?
                        String nValue = vList.getNextString();

                        NodeReference tempGlobal = connection.createNodeReference(nKey);
                        if (tempGlobal.exists(nValue)) {
                            // this is a reference
                            read(nDocument,tempGlobal.getList(nValue),(NoSchemaDocument)blueprint.get(key));
                        } else {
                            nDocument.put(nKey,nValue);
                        }
                    }
                    document.put(key,nDocument);
                // reference
                } else {
                     String nKey = valueList.getNextString();
                     document.put(key,nKey);
                }
            }
        }
    }

    /** Checks if this DocumentMap has any elements.
     *
     * @return boolean true if DocumentMap is empty, false otherwise
     */
    public boolean isEmpty() {
        return (global.exists());
    }

    /** Checks if a NoSchemaDocument identified by the specified key exists.
     *
     * @param key key
     *
     * @return boolean true if a NoSchemaDocument identified by this
     *                 key exists, false otherwise
     */
    public boolean containsKey(String key) {
        return (global.exists(key));
    }

    /** Removes (from database) a NoSchemaDocument identified by the given key.
     *
     * @param key key
     */
    public void remove(String key) {
        global.killNode(key);
    }

    /** Removes the entire DocumentMap from the database. Use
     * extreme caution as calling this method effectively erases
     * all data.
     */
    public void clear() {
        global.kill();
    }

    public NodeReference getGlobal() {
        return connection.createNodeReference(name);
    }

}
