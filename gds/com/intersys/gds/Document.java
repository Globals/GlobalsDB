package com.intersys.gds;

import java.io.StringWriter;
import java.util.HashMap;

/** <CODE>Document</CODE> is the main GDS building block. It wraps a
 * HashMap<String,Object> as a <CODE>Document</CODE> object. Key value is always a
 * String, while values can be of arbitrary types currently supported
 * by the GDS engine. On the database side, a Document is always stored
 * in a serialized form. GDS uses ValueList to serialize Java objects.
 * key is the DocumentMap key which uniquely identifies a Document.
 * For example, the following "Person" Document, given here as a JSON string:
 * { "name" : "John Smith" , "dob" : "12-10-1975", "ssn" : "105-01-9843" }
 * will be stored as a single global node:
 * ^Person(key)=$LB("John Smith","12-10-1975","105-01-9843"). Document key
 * values are not stored along with the data (values). Schema is captured
 * in ^Schema global, while indices are stored in ^Index global. In both cases,
 * globals are subscripted by the Document name. For example, Person Document
 * schema would be stored in ^Schema("Person") and Person indices would be
 * stored in ^Index("Person").
 * 
 */
public class Document extends HashMap<String,Object> {

    public Document() {}

   /** Constructor. Turns a HashMap into a Document object.
    *
    * @param map map to be wrapped as Document
    */
    public Document(HashMap map) {
        super();
        putAll(map);
    }

   /** Returns the current Document object as XML String.
    *
    * @param separator line separator
    *
    * @return String XML string representation of this Document
    */
    public String toXML(String separator) {
	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml = xml + Util.toXML(this,separator);
        return xml + separator + "</xml>";
    }

   /** Returns the current Document object as JSON String.
    *
    * @param separator line separator
    *
    * @return String JSON string representation of this Document
    */
    public String toJSON(String separator) {
        return Util.toJSON(this,separator,true);
    }
}
