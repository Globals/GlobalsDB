package com.intersys.gds.query;

import java.util.regex.Pattern;

public class PatternMatch extends Query {

    Pattern         pattern;

   /** <CODE>PatternMatch</CODE> query constructor. Takes key and pattern as parameters.
    *
    */
    public PatternMatch(String k, String patternText) {
        key = k;
        pattern = Pattern.compile(patternText);
    }

    public boolean apply(Object candidate) {
        return pattern.matcher((String)candidate).matches();
    }
}
