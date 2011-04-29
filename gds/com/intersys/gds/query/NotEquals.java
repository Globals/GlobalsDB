package com.intersys.gds.query;

/** <CODE>NotEquals</CODE> query. Returns all Documents whose values
 * corresponding to the given key do not match the given value.
 * Comparison is performed using the Java equals operator.
 *
 */
public class NotEquals extends Query {

    private Object      value;

   /** <CODE>NotEquals</CODE> query constructor. Takes key and value as parameters.
    *
    */
    public NotEquals(String k, Object v) {
        key = k;
        value = v;
    }

    public boolean apply(Object candidate) {
        return (!candidate.equals(value));
    }

}
