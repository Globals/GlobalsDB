package com.intersys.gds.query;

/** <CODE>IsNull</CODE> query. Returns all Documents whose values
 * corresponding to the given key are equal to null.
 *
 */
public class IsNull extends Query {

   /** <CODE>IsNull</CODE> query constructor. Takes key as parameter.
    *
    */
    public IsNull(String k) {
        key = k;
    }

    public boolean apply(Object candidate) {
        return (candidate == null);
    }

}
