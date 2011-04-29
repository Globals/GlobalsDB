package com.intersys.gds.query;

/** <CODE>StarsWith</CODE> query. Returns all Documents whose values
 * corresponding to the given key match the supplied startsWith pattern.
 *
 */
public class StartsWith extends Query {

    private String      pattern;

   /** <CODE>StartsWith</CODE> query constructor. Takes key and pattern as parameters.
    *
    */
    public StartsWith(String k, String p) {
        key = k;
        pattern = p;
    }

    public boolean apply(Object candidate) {
        return ((String)candidate).startsWith(pattern);
    }

}
