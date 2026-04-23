package org.javalabs.jpa.descriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concrete class that represents a sql query cache.
 * This is a singleton class.
 *
 * @author Sudiptasish Chanda
 */
public final class SqlQueryCache implements QueryCache {
    
    private static final SqlQueryCache QUERY_CACHE = new SqlQueryCache();
    
    // Map that holds the raw sql query for specific entity.
    private final Map<Class<?>, NativeQuery[]> queries = new HashMap<>();
    
    private final Lock aLock = new ReentrantLock();
    
    private SqlQueryCache() {}
    
    /**
     * Return the query cache.
     * @return SqlQueryCache
     */
    public static SqlQueryCache get() {
        return QUERY_CACHE;
    }
    
    @Override
    public void put(Class<?> entity, QueryType type, String raw) {
        try {
            aLock.lock();
            
            NativeQuery[] nq = queries.get(entity);
            if (nq == null) {
                nq = new NativeQuery[QueryType.values().length];
                queries.put(entity, nq);
            }
            nq[type.ordinal()] = new NativeSQLQuery(raw, type);
        }
        finally {
            aLock.unlock();
        }
    }

    @Override
    public NativeQuery get(Class<?> entity, QueryType type) {
        NativeQuery[] nq = queries.get(entity);
        if (nq != null) {
            return nq[type.ordinal()];
        }
        return null;
    }
    
    @Override
    public void clear() {
        queries.clear();
    }
    
    public static class NativeSQLQuery implements NativeQuery {
        
        private final String raw;
        private final QueryType type;
        
        public NativeSQLQuery(String raw, QueryType type) {
            this.raw = raw;
            this.type = type;
        }

        @Override
        public String raw() {
            return raw;
        }

        @Override
        public QueryType type() {
            return type;
        }
    }
}