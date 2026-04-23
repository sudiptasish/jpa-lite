package org.javalabs.jpa.util;

/**
 *
 * @author Sudiptasish Chanda
 */
public final class QueryHints {
    
    public static enum RetrievalStrategy {INDEX, NAME};
    
    public static final String ALLOW_NATIVE_QUERY = "allow.native.query";
    public static final String RETRIEVAL_STRATEGY = "retrieval.strategy";
    public static final String FETCH_DEF = "fetch.def";
    public static final String FETCH_TYPE = "fetch.type";
    public static final String QUERY_TYPE = "query.type";
    public static final String ENABLE_BATCH = "enable.batch";
    
}
