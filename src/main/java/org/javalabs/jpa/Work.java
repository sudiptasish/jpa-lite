package org.javalabs.jpa;

import jakarta.persistence.EntityManager;
import java.sql.Connection;

/**
 * Indicates a work that an {@link EntityManager} is performing.
 * 
 * <p>
 * Once an instance of an {@link EntityManager} is obtained, it can be used to
 * perform any database operation. Every activity that it performs, is identified
 * by {@link Work}. An {@link EntityManager} can perform multiple works for better
 * reusability. Each such task is identified by this object. Whenever a new task
 * is started, a {@link Connection} is obtained from the underlying datasource,
 * and kept alive until the task is destroyed.
 *
 * @author Sudiptasish Chanda
 */
public interface Work {
    
    /**
     * Indicate the start of a work.
     */
    void start();
    
    /**
     * Indicate whether the current task is active.
     * @return boolean
     */
    boolean active();
    
    /**
     * Return the connection object used to perform this work.
     * If the task is not yet active, then no connection object will be present,
     * and this API will return null.
     * 
     * @return Connection
     */
    Connection connection();
    
    /**
     * Indicates the completion of a task.
     * Once the task is complete, all its associated resources will be freed up,
     * and connection will be returned to pool.
     */
    void complete();
    
    /**
     * Return the entity manager this UnitOfWork is associated with.
     * @return EntityManager
     */
    EntityManager entityManager();
}
