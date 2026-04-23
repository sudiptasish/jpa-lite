package org.javalabs.jpa.descriptor;

/**
 * Query builder interface.
 *
 * @author Sudiptasish Chanda
 */
public interface QueryBuilder {
    
    /**
     * Build the select query for this entity.
     * 
     * @param clazz
     * @return String
     */
    String selectQuery(Class<?> clazz);
    
    /**
     * Build the select query for this entity.
     * 
     * @param clazz
     * @param type
     * @return String
     */
    String selectQuery(Class<?> clazz, QueryCache.QueryType type);
    
    /**
     * Build the insert query for this entity.
     * 
     * @param clazz
     * @return String
     */
    String insertQuery(Class<?> clazz);
    
    /**
     * Build the update query for this entity.
     * 
     * @param clazz
     * @return String
     */
    String updateQuery(Class<?> clazz);
    
    /**
     * Build the delete query for this entity.
     * 
     * @param clazz
     * @return String
     */
    String deleteQuery(Class<?> clazz);
}
