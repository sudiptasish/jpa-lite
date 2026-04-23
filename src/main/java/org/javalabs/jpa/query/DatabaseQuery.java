package org.javalabs.jpa.query;

import org.javalabs.jpa.BasicJdbcMechanism;
import org.javalabs.jpa.JdbcReader;
import org.javalabs.jpa.JdbcWriter;
import org.javalabs.jpa.LiteEntityManager;
import org.javalabs.jpa.Query;
import org.javalabs.jpa.QueryMechanism;
import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.descriptor.QueryBuilder;
import org.javalabs.jpa.descriptor.SQLQueryBuilder;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to represent a database query.
 * 
 * <p>
 * This class has the essential APIs to facilitate all kind of CRUD operations
 * against db. This class and any of it's implementing classes are not thread safe.
 * Every {@link JdbcReader} or {@link JdbcWriter} will have a query object 
 * associated to it. And every time the reader/writer has been called, it will
 * re-use the same query object.
 *
 * @author Sudiptasish Chanda
 */
public abstract class DatabaseQuery implements Query {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseQuery.class);    
    
    private final PersistenceHandler handler = PersistenceHandler.get();
    
    private final QueryBuilder builder = new SQLQueryBuilder();
    
    private final QueryMechanism queryMechansim = new BasicJdbcMechanism();
    
    // There are different kind of queries exist.
    // If multiple operations are performed (e.g., delete, insert, update, etc),
    // then we need to maintain the order, in which they will be executed.
    //
    // The standard order is:
    // 1. Delete
    // 2. Insert
    // 3. Update
    //
    // This variable store the order of execution of a query.
    private int order = -1;
    
    // Currently active entity type.
    // Because entity type {@link Entity} corresponds to a table, which implies 
    // that at any point of time a particular query class will be acting on specific
    // table.
    private Class<?> entity;
    
    // If verbose is on.
    // If verbose is on, then it all set to dump the usefull transaction logs.
    private boolean verbose = false;
    
    // Map to store the additional query hints, that may be provided by client.
    // It can also accept the vendor specific hint.
    protected final Map<String, Object> hints = new HashMap<>();
    
    // If verbose log is on, then the query will re-use this builder to dump the log.
    protected final StringBuilder buffer = new StringBuilder(256);
    
    private final LiteEntityManager em;
    
    protected DatabaseQuery(LiteEntityManager em) {
        this.em = em;
        verbose = LOGGER.isTraceEnabled();
    }
    
    /**
     * Return the attached entity manager.
     * @return LiteEntityManager
     */
    protected LiteEntityManager getEm() {
        return em;
    }
    
    /**
     * Return the persistence handler.
     * @return PersistenceHandler
     */
    public PersistenceHandler handler() {
        return handler;
    }

    /**
     * Return the query mechanism
     * @return NativeQueryMechanism
     */
    public QueryMechanism queryMechansim() {
        return queryMechansim;
    }
    
    /**
     * Return the query cache used by this database query object.
     * @return QueryCache
     */
    public QueryBuilder builder() {
        return builder;
    }

    /**
     * Indicate if verbose is enabled.
     * @return boolean
     */
    public boolean verbose() {
        return verbose;
    }
    
    /**
     * Add the entity type to this query object.
     * @param clazz 
     */
    public void addEntityClass(Class<?> clazz) {
        entity = clazz;
    }

    /**
     * Get the currently active entity type.
     * @return Class
     */
    public Class<?> entityClass() {
        return entity;
    }
    
    /**
     * Set the execution order of this query object.
     * @param order 
     */
    public void order(int order) {
        this.order = order;
    }
    
    /**
     * Return the execution order of this query object.
     * @return int
     */
    public int order() {
        return this.order;
    }
    
    /**
     * Add a hint.
     * This hint will be used to manipulate the retrieval strategy. One can also
     * put any vendor specific hint here, provided the underlying implementation
     * has support for it.
     * 
     * @param hintName      Name of the hint
     * @param hintValue     Hint value.
     */
    public void addHint(String hintName, Object hintValue) {
        hints.put(hintName, hintValue);
    }
    
    /**
     * Return the database query.
     * @return String
     */
    protected abstract String dbQuery();

    @Override
    public void reset() {
        entity = null;
        hints.clear();
    }
}
