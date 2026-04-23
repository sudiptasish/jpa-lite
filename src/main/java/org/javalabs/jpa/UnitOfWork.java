package org.javalabs.jpa;

import jakarta.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Indicates a unit of work to be performed.
 *
 * @author Sudiptasish Chanda
 */
public class UnitOfWork implements Work {
    
    private boolean started = false;
    
    // The underlying connection it will use.
    private Connection connection;
    
    private final LiteEntityManager em;
    
    UnitOfWork(LiteEntityManager em) {
        this.em = em;
    }

    @Override
    public void start() {
        try {
            // It will try to obtain a connection from the pool.
            // If no connection is available, then the work will be aborted.
            connection = ((LiteEntityManagerFactory)em.getEntityManagerFactory()).getPooledConnection();
            started = true;
        }
        catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public boolean active() {
        return started;
    }

    @Override
    public Connection connection() {
        if (!active()) {
            throw new IllegalStateException("No Active UnitOfWork found");
        }
        return connection;
    }

    @Override
    public void complete() {
        try {
            // It will try to obtain a connection from the pool.
            // If no connection is available, then the work will be aborted.
            ((LiteEntityManagerFactory)em.getEntityManagerFactory()).closePooledConnection(connection);
        }
        catch (SQLException e) {
            throw new JdbcException(e);
        }
        finally {
            started = false;
        }
    }

    @Override
    public EntityManager entityManager() {
        return em;
    }
    
}
