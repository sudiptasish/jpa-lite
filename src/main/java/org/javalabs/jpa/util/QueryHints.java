package org.javalabs.jpa.util;

/**
 * Defines constants and utilities for specifying query hints in JPA operations.
 *
 * <p>{@code QueryHints} provides a centralized set of keys that can be used to
 * customize the behavior of queries executed via {@link jakarta.persistence.Query}
 * or {@link jakarta.persistence.TypedQuery}. These hints allow fine-grained control
 * over performance, caching, locking, and provider-specific optimizations.</p>
 *
 * <p>Query hints are typically passed using the {@code setHint} method:</p>
 * <pre>{@code
 * Query query = entityManager.createQuery("SELECT e FROM Employee e");
 * query.setHint(QueryHints.CACHEABLE, true);
 * }</pre>
 *
 * <p>Common categories of query hints include:</p>
 * <ul>
 *   <li><b>Caching:</b> Enable or disable second-level or query cache usage</li>
 *   <li><b>Fetching:</b> Control fetch size or fetch strategies</li>
 *   <li><b>Locking:</b> Configure lock modes and timeouts</li>
 *   <li><b>Performance:</b> Tune query execution behavior</li>
 * </ul>
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
