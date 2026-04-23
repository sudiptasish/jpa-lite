package org.javalabs.jpa.descriptor;

import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;

/**
 * Store for all named queries.
 * 
 * <p>
 * A jpa entity may define {@link NamedQuery} or {@link NamedNativeQuery}s. While
 * scanning an entity, all such named queries are extracted and kept in this class.
 *
 * @author Sudiptasish Chanda
 */
public interface NamedQueryStore {
    
    /**
     * Put the named/native query attribute in the store.
     * @param query 
     */
    void put(QueryAttribute query);
    
    /**
     * Return the named/native query attribute for this name.
     * @param name
     * @return QueryAttribute
     */
    QueryAttribute get(String name);
}
