package com.intersys.gds.query;

/** <CODE>Equals</CODE> query. Returns all Documents whose values
 * corresponding to the given key match the given value. Comparison
 * is performed using the Java equals operator.
 *
 */
public class Equals extends Query {

    private Object      value;

   /** <CODE>Equals</CODE> query constructor. Takes key and value as parameters.
    *
    */
    public Equals(String k, Object v) {
        key = k;
        value = v;
    }

    public boolean apply(Object candidate) {
        return candidate.equals(value);
    }
}
