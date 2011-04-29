package com.intersys.gds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import java.util.LinkedHashMap;

/** <CODE>DocumentType</CODE> is used to declare and access Document
 * schema for a full schema version of GDS. In addition to the
 * declaration of all currently supported types, it provides methods
 * to create and access Document types and indices. GDS supports
 * indexing of individual fields (compound indices are not currently
 * supported. There is no limit on number of indices, but make sure
 * to use them smartly. Indices will dramatically improve query
 * performance but too many indices can also significantly slow down
 * the inserts, since indexing is done synchronously.
 *
 */

public class DocumentType {

    String                              name;
    LinkedHashMap<String,ElementType>   types;
    List<String>                        indices;
    HashMap<String,Boolean>             isUniqueIndex;

    private DocumentType(String n) {
        name = n;
        types = new LinkedHashMap<String,ElementType>();
        indices = new ArrayList<String>();
        isUniqueIndex = new HashMap<String,Boolean>();
    }

   /** Create a new DocumentType object.
    *
    * @param name DocumentType name
    *
    * @return DocumentType return a newly created DocumentType
    */
    public static DocumentType createDocumentType(String name) {
        return new DocumentType(name);
    }

   /** Create a new DocumentType object. This flavor takes a sample
    * document which is then used to infer types. A new DocumentType
    * object is created and prefilled with type info corresponding
    * to the key/value pairs found in the sample document. Data type
    * entries, as well as data type array entries are mapped to the
    * nearest type, while object types are projected as embedded types
    * by default. These mappings can later be overridden by calling
    * individual setter methods.
    *
    * @param name DocumentType name
    * @param document sample document
    *
    * @return DocumentType return a newly created DocumentType
    */
    public static DocumentType createDocumentType(String name, Document document) {
        DocumentType type = new DocumentType(name);
        type.initializeType(document);
        return type;
    }

   /** Create a new DocumentType subtype. This method is to be
    * used for embedded Documents
    *
    * @return DocumentType return a newly created subtype
    */
    public static DocumentType createSubtype() {
        return new DocumentType(null);
    }
    
   /** Create a new DocumentType subtype. This method is to be
    * used for embedded Documents. A new DocumentType
    * object is created and prefilled with type info corresponding
    * to the key/value pairs found in the sample document. Data type
    * entries, as well as data type array entries are mapped to the
    * nearest type, while object types are projected as embedded types
    * by default. These mappings can later be overridden by calling
    * individual setter methods.
    *
    * @param document sample document
    * @return DocumentType return a newly created subtype
    */
    public static DocumentType createSubtype(Document document) {
        DocumentType type = new DocumentType(null);
        type.initializeType(document);
        return type;
    }

   /** Add a new simple (datatype) key value pair.
    *
    * @param name key name
    * @param type value type
    *
    */
    public void setDatatype(String name, int type) {
        types.put(name,new ElementType(name,type));
    }
    
   /** Sets embedded key value pair.
    *
    * @param name key name
    * @param subtype embedded object type
    * @param isArray true if array type
    *
    */
    public void setEmbedded(String name, DocumentType subtype, boolean isArray) {
        if (isArray) {
            types.put(name,new ElementType(name,ElementType.TYPE_EMBEDDED_ARRAY,subtype));
        } else {
            types.put(name,new ElementType(name,ElementType.TYPE_EMBEDDED,subtype));
        }
    }
    
   /** Sets a new reference key value pair. Type specified must
    * be either TYPE_REFERENCE or TYPE_REFERENCE_ARRAY
    *
    * @param name key name
    * @param type value type
    * @param reference reference name
    * @param key reference key
    *
    */
    public void setReference(String name, int type , String reference, String key) {
        if ((type != ElementType.TYPE_REFERENCE) && (type != ElementType.TYPE_REFERENCE_ARRAY) && (type != ElementType.TYPE_BACK_REFERENCE)) {
            throw new RuntimeException("Illegal type: " + type);
        }
        types.put(name,new ElementType(name,type,reference,key));
    }

   /** Designates the field as an index. Any number of individual
    * indices can be created. Compound indices (two or more fields)
    * are currently not supported. Indexing can currently be created
    * on datatype fields only; attempt to index any other field will
    * silently fail. Indices can be designated as unique.
    *
    * @param fieldName index field name
    * @param unique true if unique index
    *
    */
    public void addIndex(String fieldName, boolean unique) {
        if (!indices.contains(fieldName)) {
            indices.add(fieldName);
            isUniqueIndex.put(fieldName,unique);
        }
    }

    private void initializeType(Document document) {
        for (String key : document.keySet()) {
            Object value = document.get(key);
            if (value == null) {
                continue;
            }
            Class clazz = value.getClass();
            if (clazz == String.class) {
                types.put(key,new ElementType(key,ElementType.TYPE_STRING));
            } else if (Util.isPrimitiveWrapper(clazz)) {
                types.put(key,new ElementType(key,clazz));
            } else if (value instanceof String[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_STRING_ARRAY));
            } else if (value instanceof int[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_INTEGER_ARRAY));
            } else if (value instanceof Integer[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_INTEGER_WRAPPER_ARRAY));
            } else if (value instanceof long[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_LONG_ARRAY));
            } else if (value instanceof Long[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_LONG_WRAPPER_ARRAY));
            } else if (value instanceof double[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_DOUBLE_ARRAY));
            } else if (value instanceof byte[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_BYTE_ARRAY));
            } else if (value instanceof Double[]) {
                types.put(key,new ElementType(key,ElementType.TYPE_DOUBLE_WRAPPER_ARRAY));
            } else if (value instanceof Document) {
                DocumentType subtype = new DocumentType(null);
                subtype.initializeType((Document)value);
                types.put(key,new ElementType(key,ElementType.TYPE_EMBEDDED,subtype));          
            } else if (value instanceof Document[]) {
                DocumentType subtype = new DocumentType(null);
                subtype.initializeType(((Document[])value)[0]);
                types.put(key,new ElementType(key,ElementType.TYPE_EMBEDDED_ARRAY,subtype));
            }
        }
    }

    void store(Connection connection, ValueList list) {
        list.append(types.size());
        ValueList nestedList = connection.createList();
        for (String eName : types.keySet()) {
            ElementType type = types.get(eName);
            list.append(eName);
            list.append(type.type);
            DocumentType nested = type.nestedType;
            if (nested != null) {
                nestedList.clear();
                nested.store(connection,nestedList);
                list.append(nestedList);
            }
            String ref = type.reference;
            if (ref != null) {
                list.append(ref);
                list.append(type.referenceKey);
            }
        }
    }

    void load(Connection connection, ValueList list) {
        int count = list.getNextInt();
        types.clear();
        for (int i=0;i<count;i++) {
            String eName = list.getNextString();
            int eType = list.getNextInt();
            ElementType type = new ElementType(eName,eType);
            if ((eType == ElementType.TYPE_EMBEDDED) ||
               (eType == ElementType.TYPE_EMBEDDED_ARRAY)) {
                DocumentType dt = new DocumentType(null);
                ValueList ml = list.getNextList();
                dt.load(connection,ml);
                type.nestedType = dt;
            } else if ((eType == ElementType.TYPE_REFERENCE) ||
                       (eType == ElementType.TYPE_REFERENCE_ARRAY)) {
                type.reference = list.getNextString();
                type.referenceKey = list.getNextString();
                DocumentType dt = new DocumentType(type.reference);
                NodeReference schemaGlobal = connection.createNodeReference("Schema");

                ValueList ml = schemaGlobal.getList(type.reference);
                dt.load(connection,ml);
                type.nestedType = dt;
            // TODO
            } else if (eType == ElementType.TYPE_BACK_REFERENCE) {
                String refName = list.getNextString();
                String bRef = list.getNextString();
            }
            types.put(eName,type);
        }
        NodeReference indexGlobal = connection.createNodeReference("Indices");
        if (name != null) {
            indexGlobal.appendSubscript(name);
            loadIndices(indexGlobal);
        }
     }

    void storeIndices(NodeReference indexGlobal, ValueList list) {
        list.clear();
        for (int i=0;i<indices.size();i++) {
            String iName = indices.get(i);
            list.append(iName);
            if (isUniqueIndex.get(iName)) {
                list.append(1);
            } else {
                list.append(0);
            }
        }
        indexGlobal.set(list);
    }

    void loadIndices(NodeReference indexGlobal) {
        if (!indexGlobal.exists()) {
            return;
        }
        indices = new ArrayList<String>();
        isUniqueIndex = new HashMap<String,Boolean>();
        ValueList list = indexGlobal.getList();
        for (int i=0;i<list.length()/2;i++) {
            String iName = list.getNextString();
            indices.add(iName);
            if (list.getNextInt() == 1) {
                isUniqueIndex.put(iName,Boolean.TRUE);
            } else {
                isUniqueIndex.put(iName,Boolean.FALSE);
            }
        }
    }

    public int getType(String fieldName) {
        ElementType t = types.get(fieldName);
        if (t == null) {
            throw new RuntimeException("No such field: " + fieldName);
        }
        return t.type;
    }

    public boolean isIndexUnique(String name) {
        return isUniqueIndex.get(name);
    }
}
