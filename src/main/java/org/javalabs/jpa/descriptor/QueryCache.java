package org.javalabs.jpa.descriptor;

/**
 * Internal cache to store the generated and pre-compiled sql queries.
 * 
 * <p>
 * RDBMS system does not understand any jpa entity. Therefore, in order to communicaate
 * with the underlying RDBMS system, the JPA-LiTE Framework must generate the
 * appropriate sql-92 queries. The query generayion is a costly process. Hence,
 * the generated queries are stored in an in-memory cache for optimization.
 * 
 * <p>
 * Every RDBMS table will have the following basic queries:
 * <ul>
 *   <li> Select Query </li>
 *   <li> Insert Query </li>
 *   <li> Update Query </li>
 *   <li> Delete Query </li>
 *   <li> Select Query with Lock </li>
 *   <li> Relationship Select Query </li>
 * </ul>
 * 
 * The cache will store all 4 kinds of basic queries for every underlying table.
 *
 * @author Sudiptasish Chanda
 */
public interface QueryCache {
    
    enum QueryType {
        SELECT_ALL,
        SELECT,
        INSERT,
        UPDATE,
        DELETE,
        SELECT_LOCK,
        SELECT_REL,
        SELECT_REL_ALL
    };
    
    /**
     * Add the sql query in the query cache against the entity specified.
     * Depending on the {@link QueryType}, the raw query will be kept in
     * appropriate slot.
     * 
     * @param entity    JPA entity
     * @param type      Type of the query, SELECT, INSERT, UPDATE or DELETE.
     * @param raw       Raw SQL query.
     */
    void put(Class<?> entity, QueryType type, String raw);
    
    /**
     * Return the query as identified by the query type for the given entity.
     * The cache has four slots for all different kind of query type. Depending
     * on the type requested, the query element from the appropriate slot is picked
     * up and returned to caller.
     * 
     * @param entity    Entity for which the query will be fetched.
     * @param type      Type of the query.
     * @return NativeQuery   The SQL query.
     */
    NativeQuery get(Class<?> entity, QueryType type);
    
    /**
     * Clear this query cache.
     */
    void clear();
    
    /**
     * NativeQuery is the cache element.
     */
    interface NativeQuery {
        
        /**
         * Return the raw sql query.
         * @return String
         */
        String raw();
        
        /**
         * Return the type of the query.
         * @return QueryType
         */
        QueryType type();
    }
}
