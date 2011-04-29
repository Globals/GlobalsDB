package com.intersys.gds.noschema;

import java.util.HashMap;

import com.intersys.gds.Document;

/** <CODE>NoSchemaDocument</CODE> is a no schema version of GDS Document.
 * For example, in the no schema version of Document, on the database
 * side, a "Person" Document, represented by the following JSON document:
 * { "name" : "John Smith" , "dob" : "12-10-1975", "ssn" : "105-01-9843" }
 * will be stored as a single global node:
 * ^Person(key)=$LB("name","John Smith","dob","12-10-1975","ssn","105-01-9843")
 * key is the DocumentMap key which uniquely identifies a Document.
 *
 */
public class NoSchemaDocument extends Document {

    private     String          dbID =  null;
    private     String          name;

    public NoSchemaDocument() {
        super();
    }

    NoSchemaDocument(HashMap map) {
        super(map);
    }

    String getDBID() {
        return dbID;
    }

    void setDBID(String n, Object id) {
        dbID = id.toString();
        name = n;
    }

    String getName() {
        return name;
    }

}

