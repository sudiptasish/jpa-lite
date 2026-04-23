package org.javalabs.jpa.query;

/**
 * This is a query builder pool.
 * 
 * <p>
 * The conventional <code>NamedNativeQuery</code>s are cached by jpa-lite framework.
 * However, for criteria API, it requires to generate the raw SQL statement each
 * and every time the criteria API is called. The API uses traditional {@link QueryBuffer}
 * to generate the query. Disposing the {@link QueryBuffer} every time a query is
 * successfully executed leads to memory fragmentation. Hence, the {@link SBPool}
 * class was introduced which will pool these {@link QueryBuffer}s.
 * 
 * <p>
 * The criteria API will check the pool to see if any unused {@link QueryBuffer}
 * is available in the pool. If so, uses it while generating the raw sql statement.
 * 
 * <p>
 * The pool has a fixed size. If the {@link #lookup() } method is invoked and there
 * is no available {@link QueryBuffer}, then a new on-demand {@link QueryBuffer}
 * object will be created and returned. However, note that, this newly created
 * {@link QueryBuffer} will become eligible for garbage collection immediately 
 * after it's use.
 *
 * @author schan280
 */
public interface SBPool {
    
    /**
     * Lookup the pool to obtain an unused {@link QueryBuffer}.
     * If the pool has no available {@link QueryBuffer}, then a new {@link QueryBuffer}
     * object will be created and returned.
     * 
     * @return QueryBuffer
     */
    QueryBuffer lookup();
    
    /**
     * Release this {@link QueryBuffer} to the pool.
     * This API will clear the internal content of the {@link QueryBuffer} before
     * releasing it to a pool.
     * 
     * @param buff  QueryBuffer to be released.
     */
    void release(QueryBuffer buff);
}
