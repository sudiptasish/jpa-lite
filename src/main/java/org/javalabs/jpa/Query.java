package org.javalabs.jpa;

import jakarta.persistence.EntityManager;
import java.sql.SQLException;

/**
 * Base interface to represent a query executor.
 * 
 * <p>
 * Instance of this class is not thread safe. A {@link Query} object is tied up
 * with an {@link EntityManager}. As long as the {@link EntityManager} is active,
 * for any number of database calls, it will reuse the {@link Query} object again
 * and again. After every execution of {@link Query} object, it's {@link Query#reset() }
 * method must be called to reset the internal state of this object, thus making it
 * eligible for subsequent db call.
 *
 * @author Sudiptasish Chanda
 */
public interface Query {
    
    /**
     * Execute a database query and return the result.
     * Here it is expected that the query to be build by the underlying framework
     * and made it available to this API.
     * 
     * @return Object   Result set object.
     * @throws SQLException
     */
    Object execute() throws SQLException;
    
    /**
     * Execute a database query and return the result.
     * This API accepts the raw sql query and executes it against the DB.
     * 
     * @param query     SQL query to be executed.
     * @return Object   Result set object.
     * @throws SQLException
     */
    Object execute(String query) throws SQLException;
    
    /**
     * Reset the state of this query object.
     */
    void reset();
}
