package com.intersys.gds.query;

import java.util.HashSet;
import com.intersys.gds.DocumentMap;

/** <CODE>And</CODE> query. Executes two sub-queries then merges the
 * results using the AND logical operand.
 *
 */
public class And extends Query {

    private Query       query1;
    private Query       query2;

   /** <CODE>And</CODE> query constructor. Takes two sub-queries as parameters.
    *
    */
    public And(Query q1, Query q2) {
        query1 = q1;
        query2 = q2;
    }

    public void execute(DocumentMap dm) {
        documentMap = dm;
        candidates = new HashSet<String>();
        if ((query1 == null) || (query2 == null)) {
            throw new RuntimeException("Invalid query");
        }
        /* this seems to be faster than using executor.invokeAll (see Or query)
        Future t1 = documentMap.executor.submit(new ConcurrentQuery(query1,documentMap));
        Future t2 = documentMap.executor.submit(new ConcurrentQuery(query2,documentMap));
        while (true) {
            if (t1.isDone() && t2.isDone()) {
                break;
            }
        }
        */

        query1.execute(documentMap);
        query2.execute(documentMap);
        if ((query1.candidates == null) || query1.candidates.isEmpty()) {
            return;
        }
        if ((query2.candidates == null) || query2.candidates.isEmpty()) {
            return;
        }
        for (String cand: query1.candidates) {
            if (query2.candidates.contains(cand)) {
                candidates.add(cand);
            }
        }
        candidateIt = candidates.iterator();
    }

    public boolean apply(Object candidate) {
        throw new UnsupportedOperationException("Not applicable for logical expressions.");
    }

}
