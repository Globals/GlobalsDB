package com.intersys.gds.query;

import com.intersys.gds.DocumentMap;

public class ConcurrentQuery implements Runnable {

    private  Query           query;
    private  DocumentMap     documentMap;

    ConcurrentQuery(Query q, DocumentMap dm) {
        query = q;
        documentMap = dm;
    }

    public void run() {
        query.execute(documentMap);
    }
}
