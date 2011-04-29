package com.intersys.gds.query;

import java.util.HashSet;

import com.intersys.gds.DocumentMap;

/** <CODE>Or</CODE> query. Executes two sub-queries then merges the
 * results using the OR logical operand.
 *
 */
public class Or extends Query {

    private Query       query1;
    private Query       query2;

   /** <CODE>Or</CODE> query constructor. Takes two sub-queries as parameters.
    *
    */
    public Or(Query q1, Query q2) {
        query1 = q1;
        query2 = q2;
    }

    public void execute(DocumentMap dm) {
        documentMap = dm;
        candidates = new HashSet<String>();
        if ((query1 == null) || (query2 == null)) {
            throw new RuntimeException("Invalid query");
        }
        /*
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        tasks.add(Executors.callable(new CallableTask(query1,documentMap)));
        tasks.add(Executors.callable(new CallableTask(query2,documentMap)));
        try {
            documentMap.executor.invokeAll(tasks);
        } catch (InterruptedException ex) {
        }
        */
        query1.execute(documentMap);
        query2.execute(documentMap);
        candidates.addAll(query1.candidates);
        candidates.addAll(query2.candidates);
        candidateIt = candidates.iterator();
    }

    public boolean apply(Object candidate) {
        throw new UnsupportedOperationException("Not applicable for logical expressions.");
    }

}
