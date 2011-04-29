package com.intersys.gds.query;

/** <CODE>NotNull</CODE> query. Returns all Documents whose values
 * corresponding to the given key are not null.
 *
 */
public class NotNull extends Query {

   /** <CODE>NotNull</CODE> query constructor. Takes key as parameter.
    *
    */
    public NotNull(String k) {
        key = k;
    }

    public boolean apply(Object candidate) {
        return (candidate != null);
    }

}
