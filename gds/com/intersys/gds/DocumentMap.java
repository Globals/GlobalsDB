package com.intersys.gds;

import java.util.ArrayList;
import java.util.HashMap;

import com.intersys.gds.query.Query;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import java.lang.reflect.Array;
import java.util.Set;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

/** <CODE>DocumentMap</CODE> is the principal GDS building block.
 * It provides methods to store, load and run queries which return
 * <CODE>Document</CODE> types. <CODE>DocumentMap</CODE> is mapped
 * as a global. For example, a <CODE>DocumentMap</CODE> called "Person"
 * will be mapped as ^Person. Keys used to access individual Documents
 * correspond to global subscripts, and each <CODE>Document</CODE> is
 * a global node entry. For example, the following
 * "Person" Document, identified by key, and given here as a JSON string:
 * { "name" : "John Smith" , "dob" : "12-10-1975", "ssn" : "105-01-9843" }
 * will be stored as a single global node:
 * ^Person(key)=$LB("John Smith","12-10-1975","105-01-9843").
 * Document schema is declared using <CODE>DocumentType</CODE>
 */

public class DocumentMap {

    private DocumentType                    documentType;
    private Connection                      connection;
    private NodeReference                   global;
    private NodeReference                   indexGlobal;
    private String                          name;
    private ValueList                       valueList;
    private ValueList                       writeList;
    private ValueList                       scratchList;
    private ValueList                       referenceTempList;
    private HashMap<String,NodeReference>   refGlobalMap;
    private HashMap<String,NodeReference>   indexGlobalMap;
    HashMap<String,HashMap<Object,Object>>  allIndices;
    HashMap<Object,Object>                  indexMap;
    //public  ExecutorService                 executor;

    DocumentMap(String n, Connection conn) {
        connection = conn;
        name = n;
        global = connection.createNodeReference(name);
        indexGlobal = connection.createNodeReference(name+"I");
        valueList = connection.createList();
        scratchList = connection.createList();
        writeList = connection.createList();
        referenceTempList = connection.createList();
        refGlobalMap = new HashMap<String,NodeReference>();
        indexGlobalMap = new HashMap<String,NodeReference>();
        setDocumentType(name);
        allIndices = new HashMap<String,HashMap<Object,Object>>();
        if (documentType.indices != null) {
            for (int i=0;i<documentType.indices.size();i++) {
                allIndices.put(documentType.indices.get(i),new HashMap<Object,Object>());
            }
        }
        //executor = Executors.newCachedThreadPool();
    }

    public void buildIndices() {
        if ((allIndices == null) || allIndices.isEmpty()) {
            return;
        }
        for (String indexName : allIndices.keySet()) {
            indexMap = allIndices.get(indexName);
            indexGlobal.setSubscriptCount(0);
            indexGlobal.appendSubscript(indexName);
            for (Object value : indexMap.keySet()) {
                Object key = indexMap.get(value);
                if (key instanceof ArrayList) {
                    scratchList.clear();
                    for (Object data : (ArrayList)key) {
                        scratchList.append(data);
                    }
                    indexGlobal.set(scratchList,value);
                } else {
                    indexGlobal.set((String)key,value);
                }
            }
        }
    }

    /** Stores a Document. If store succeeds, the Document is also
     * removed from the runtime memory.
     *
     * @param key key
     * @param value Document to store
     *
     */
     public void store(String key, Document value) {
        //if (documentType == null) {
        //    setDocumentType(name);
        //}
        // TODO if DocumenType == null exception?
        valueList.clear();
        write(key,value,documentType,valueList);
        global.set(valueList,key);
     }

    /** Loads a Document given it's key. Returns the
     * corresponding Document, or null if there isn't one.
     *
     * @param key key
     *
     * @return Document Document
     */
    public Document load(String key) {
        //if (documentType == null) {
        //    setDocumentType(name);
        //}
        valueList = global.getList(valueList,key);
        Document document = new Document();
        read(document,valueList,documentType);
        return document;
    }

    /** Executes an GDS ad-hoc Query. Refer to com.intersys.gds.query
     * package for the currently supported Query types and other info.
     *
     * @param query Query
     *
     */
    public void executeQuery(Query query) {
        query.execute(this);
    }

    /** Checks if this DocumentMap has any elements.
     *
     * @return boolean true if DocumentMap is empty, false otherwise
     */
    public boolean isEmpty() {
        return (global.exists());
    }

    /** Checks if a Document identified by the specified key exists.
     *
     * @param key key
     *
     * @return boolean true if a Document identified by this
     *                 key exists, false otherwise
     */
    public boolean containsKey(String key) {
        return global.exists(key);
    }

    /** Removes (from database) a Document identified by the given key.
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
        indexGlobal.kill();
    }

    /** Returns the current connection object.
     * @return Connection current connection
     */
    public Connection getConnection() {
        return connection;
    }

    /** Returns the name of this DocumentMap.
     * @return String DocumentMap name
     */
    public String getName() {
        return name;
    }

   /** Returns the corresponding DocumentType.
    * @return DocumentType DocumentType object
    */
    public DocumentType getDocumentType() {
        return documentType;
    }
   
    private void setDocumentType(String name) {
        NodeReference schemaGlobal = connection.createNodeReference("Schema");
        schemaGlobal.appendSubscript(name);
        if (!schemaGlobal.exists()) {
            documentType = null;
            return;
        }
        documentType = DocumentType.createDocumentType(name);
        documentType.load(connection,schemaGlobal.getList());
    }

    private void read(Document document, ValueList valueList, DocumentType documentType) {
        Set<String> keySet = documentType.types.keySet();
        for (String key : keySet) {
            ElementType eType = documentType.types.get(key);
            int type = eType.type;
            if (type == ElementType.TYPE_STRING) {
                document.put(key,valueList.getNextString());
            } else if (type == ElementType.TYPE_INTEGER) {
                document.put(key,valueList.getNextInt());
            } else if (type == ElementType.TYPE_DOUBLE) {
                document.put(key,valueList.getNextDouble());
            } else if (type == ElementType.TYPE_LONG) {
                document.put(key,valueList.getNextLong());
            } else if (type == ElementType.TYPE_INTEGER_WRAPPER) {
                document.put(key,valueList.getNextInt());
            } else if (type == ElementType.TYPE_DOUBLE_WRAPPER) {
                document.put(key,valueList.getNextDouble());
            } else if (type == ElementType.TYPE_LONG_WRAPPER) {
                document.put(key,valueList.getNextLong());
            } else if (type == ElementType.TYPE_EMBEDDED) {
                ValueList tempList = valueList.getNextList();
                Document nestedDoc = new Document();
                read(nestedDoc,tempList,eType.nestedType);
                document.put(key,nestedDoc);
            } else if (type == ElementType.TYPE_EMBEDDED_ARRAY) {
            //} TODO else if (type == DocumentType.TYPE_REFERENCE_ARRAY) {
                ValueList tempList = valueList.getNextList();
                if (tempList == null) {
                    document.put(key,null);
                    continue;
                }
                int listLen = tempList.length();
                Document[] references = new Document[listLen];
                for (int j=0;j<listLen;j++) {
                    references[j] = new Document();
                    ValueList tList = tempList.getNextList();
                    read(references[j],tList,eType.nestedType);
                }
                document.put(key,references);
            } else if (type == ElementType.TYPE_REFERENCE) {
                String address = valueList.getNextString();
                if (address.equals("")) {
                    document.put(key,null);
                    continue;
                }
                Document reference = readReference(address,eType.reference,eType.nestedType);
                document.put(key,reference);
            } else if (type == ElementType.TYPE_STRING_ARRAY) {
                readStringArray(document,key);
            } else if (type == ElementType.TYPE_INTEGER_ARRAY) {
                readIntArray(document,key);
            } else if (type == ElementType.TYPE_INTEGER_WRAPPER_ARRAY) {
                readIntegerWrapperArray(document,key);
            } else if (type == ElementType.TYPE_LONG_ARRAY) {
                readLongArray(document,key);
            } else if (type == ElementType.TYPE_LONG_WRAPPER_ARRAY) {
                readLongWrapperArray(document,key);
            } else if (type == ElementType.TYPE_DOUBLE_ARRAY) {
                readDoubleArray(document,key);
            } else if (type == ElementType.TYPE_DOUBLE_WRAPPER_ARRAY) {
                readDoubleWrapperArray(document,key);
            } else if (type == ElementType.TYPE_BYTE_ARRAY) {
                document.put(key,valueList.getNextBytes());
            } else {
                document.put(key,valueList.getNextObject());
            }
        }
    }

    private void write(String key, Document document, DocumentType type, ValueList valueList) {
        Set<String> keySet = type.types.keySet();
        for (String keyName : keySet) {
            ElementType elType = type.types.get(keyName);
            Object value = document.get(keyName);
            if (value == null) {
                valueList.append((String)null);
                continue;
            }
            if (elType.isBasicType()) {
                valueList.append(value);
                // for now only datatypes can be indexed
                indexMap = allIndices.get(keyName);
                if (indexMap != null) {
                    setIndex(keyName,value,key);
                }
             } else if ((elType.type == ElementType.TYPE_REFERENCE) ||
                      (elType.type == ElementType.TYPE_REFERENCE_ARRAY)) {
                String dbid = writeReference(key,(Document)value,elType.nestedType,elType.reference,elType.referenceKey);
                valueList.append(dbid);
            } else if (value instanceof Document) {
                writeDocument(key,(Document)value,elType.nestedType,valueList,scratchList);
            } else if (value instanceof Document[]) {
                scratchList.clear();
                writeList.clear();
                for (int j=0;j<((Document[])value).length;j++) {
                    writeDocument(key,((Document[])value)[j],elType.nestedType,scratchList,writeList);
                }
                valueList.append(scratchList);
            } else if (elType.type == ElementType.TYPE_STRING_ARRAY) {
                writeStringArray((String[])value);
            } else if ((elType.type == ElementType.TYPE_INTEGER_ARRAY) ||
                       (elType.type == ElementType.TYPE_INTEGER_WRAPPER_ARRAY)) {
                writeIntArray(value,elType.type);
            } else if ((elType.type == ElementType.TYPE_LONG_ARRAY) ||
                       (elType.type == ElementType.TYPE_LONG_WRAPPER_ARRAY)) {
                writeLongArray(value,elType.type);
            } else if ((elType.type == ElementType.TYPE_DOUBLE_ARRAY) ||
                       (elType.type == ElementType.TYPE_DOUBLE_WRAPPER_ARRAY)) {
                writeDoubleArray(value,elType.type);
            } else { //if (type == ElementType.TYPE_BACK_REFERENCE) {
                valueList.append(value);
            }
        }
    }

    private void writeDocument(String key, Document doc, DocumentType type, ValueList valueList, ValueList scratch) {
        scratch.clear();
        write(key,doc,type,scratch);
        valueList.append(scratch);
    }

    private Document readReference(String address, String globalName, DocumentType dt) {
        Document reference = new Document();
        NodeReference refGlobal = getReferenceGlobal(globalName);
        refGlobal.appendSubscript(address);
        read(reference,refGlobal.getList(),dt);
        return reference;
    }

    private void setIndex(String name, Object value, String key) {
        boolean isUnique = documentType.isUniqueIndex.get(name);
        Object oldKey = indexMap.get(value);
        if (oldKey == null) {
            if (isUnique) {
                indexMap.put(value,key);
            } else {
                ArrayList<String> values = new ArrayList<String>();
                values.add(key);
                indexMap.put(value,values);
            }
        } else {
            if (isUnique) {
                return;
                //throw new RuntimeException("Duplicate unique key entry");
            }
            ArrayList<String> values = (ArrayList<String>) oldKey;
            values.add(key);
        }
    }

    private String writeReference(String key, Document document, DocumentType type, String reference, String refKey) {
        String referenceKey = document.get(refKey).toString();
        NodeReference refGlobal = getReferenceGlobal(reference);
        refGlobal.appendSubscript(referenceKey);
        if (refGlobal.exists()) {
            return referenceKey;
        }
        referenceTempList.clear();
        NodeReference oldIndexGlobal = indexGlobal;
        indexGlobal = getIndexGlobal(reference);
        write(key,document,type,referenceTempList);
        //document.setDBID(name,key);
        indexGlobal = oldIndexGlobal;
        refGlobal.set(referenceTempList);
        return referenceKey; //document.getDBID();
    }

    NodeReference getReferenceGlobal(String name) {
        NodeReference refGlobal = refGlobalMap.get(name);
        if (refGlobal == null) {
            refGlobal = connection.createNodeReference(name);
            refGlobalMap.put(name,refGlobal);
        }
        refGlobal.setSubscriptCount(0);
        return refGlobal;
    }

    NodeReference getIndexGlobal(String name) {
        NodeReference iGlobal = indexGlobalMap.get(name);
        if (iGlobal == null) {
            iGlobal = connection.createNodeReference(name+"I");
            indexGlobalMap.put(name,iGlobal);
        }
        return iGlobal;
    }

    public void close() {
        //executor.shutdown();
    }

    private void writeStringArray(String[] value) {
        writeList.clear();
        int len = Array.getLength(value);
        if (len > 0) {
            for (int i=0;i<len;i++) {
                writeList.append(value[i]);
            }
        }
        valueList.append(writeList);
    }

    private void writeDoubleArray(Object value, int type) {
        writeList.clear();
        int len = Array.getLength(value);
        if (len > 0) {
            if (type == ElementType.TYPE_DOUBLE_ARRAY) {
                for (int i=0;i<len;i++) {
                    writeList.append(Array.getDouble(value,i));
                }
            } else if (type == ElementType.TYPE_DOUBLE_WRAPPER_ARRAY) {
                for (int i=0;i<len;i++) {
                    writeList.append(((Double[])value)[i]);
                }
            }
        }
        valueList.append(writeList);
    }

    private void writeLongArray(Object value, int type) {
        writeList.clear();
        int len = Array.getLength(value);
        if (len > 0) {
            if (type == ElementType.TYPE_LONG_ARRAY) {
                for (int i=0;i<len;i++) {
                    writeList.append(Array.getLong(value,i));
                }
            } else if (type == ElementType.TYPE_LONG_WRAPPER_ARRAY) {
                for (int i=0;i<len;i++) {
                    writeList.append(((Long[])value)[i]);
                }
            }
        }
        valueList.append(writeList);
    }

    private void writeIntArray(Object value, int type) {
        writeList.clear();
        int len = Array.getLength(value);
        if (len > 0) {
            if (type == ElementType.TYPE_INTEGER_ARRAY) {
                for (int i=0;i<len;i++) {
                    writeList.append(Array.getInt(value,i));
                }
            } else if (type == ElementType.TYPE_INTEGER_WRAPPER_ARRAY) {
                for (int i=0;i<len;i++) {
                    writeList.append(((Integer[])value)[i]);
                }
            }
        }
        valueList.append(writeList);
    }

    private void readDatatypeArray(int type, Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        Object[] array =  null;
        if (type == ElementType.TYPE_STRING_ARRAY) {
            array = new String[len];
            for (int i=0;i<len;i++) {
                array[i] = tempList.getNextString();
            }
        } else if (type == ElementType.TYPE_INTEGER_ARRAY) {
            array = new Integer[len];
            for (int i=0;i<len;i++) {
                array[i] = tempList.getNextInt();
            }
        } else if (type == ElementType.TYPE_LONG_ARRAY) {
            array = new Long[len];
            for (int i=0;i<len;i++) {
                array[i] = tempList.getNextLong();
            }
        } else if (type == ElementType.TYPE_DOUBLE_ARRAY) {
            array = new Double[len];
            for (int i=0;i<len;i++) {
                array[i] = tempList.getNextDouble();
            }
        }
        document.put(key,array);
    }

    private void readStringArray(Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        String[] array = new String[len];
        for (int i=0;i<len;i++) {
            array[i] = tempList.getNextString();
        }
        document.put(key,array);
    }

    private void readIntArray(Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        int[] array = new int[len];
        for (int i=0;i<len;i++) {
            array[i] = tempList.getNextInt();
        }
        document.put(key,array);
    }

    private void readIntegerWrapperArray(Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        Integer[] array = new Integer[len];
        for (int i=0;i<len;i++) {
            array[i] = tempList.getNextInt();
        }
        document.put(key,array);
    }

    private void readDoubleArray(Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        double[] array = new double[len];
        for (int i=0;i<len;i++) {
            array[i] = tempList.getNextDouble();
        }
        document.put(key,array);
    }

    private void readDoubleWrapperArray(Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        Double[] array = new Double[len];
        for (int i=0;i<len;i++) {
            array[i] = tempList.getNextDouble();
        }
        document.put(key,array);
    }

    private void readLongArray(Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        long[] array = new long[len];
        for (int i=0;i<len;i++) {
            array[i] = tempList.getNextLong();
        }
        document.put(key,array);
    }

    private void readLongWrapperArray(Document document, String key) {
        ValueList tempList = valueList.getNextList();
        if (tempList == null) {
            document.put(key,null);
            return;
        }
        int len = tempList.length();
        Long[] array = new Long[len];
        for (int i=0;i<len;i++) {
            array[i] = tempList.getNextLong();
        }
        document.put(key,array);
    }
}
