package org.javalabs.jpa;

import org.javalabs.jpa.util.MD5HashGenerator;
import jakarta.persistence.EntityTransaction;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete class to represent a transaction.
 * 
 * <p>
 * A database transaction symbolizes a unit of work performed within a database 
 * management system against a database, and treated in a coherent and reliable 
 * way independent of other transactions. A transaction generally represents any 
 * change in a database. The {@link #begin() } method will start a new transaction.
 * Whenever client calls {@link #commit() } or {@link #rollback() } API, it is
 * assumed that the transaction has been complete. You can start a fresh new
 * transaction by calling the {@link #begin() } method again.
 *
 * @author Sudiptasish Chanda
 */
public class EntityTransactionImpl implements EntityTransaction {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityTransactionImpl.class);
    
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    
    // The work this transaction is associated with.
    private final Work work;
    
    private boolean started = false;
    private boolean rollbackOnly = false;
    
    private String txnId;
    
    EntityTransactionImpl(Work work) {
        this.work = work;
    }

    @Override
    public void begin() {
        try {
            txnId = "txn-" + MD5HashGenerator.digest(
                String.valueOf(System.identityHashCode(this))
                , String.valueOf(System.currentTimeMillis())
                , String.valueOf(COUNTER.getAndIncrement()));

            work.connection().setAutoCommit(false);
            started = true;
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("A new transaction {} has begun", txnId);
            }
        }
        catch (SQLException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Error starting new transaction", e);
            }
            throw new JdbcException(e);
        }
    }

    @Override
    public void commit() {
        try {
            if (!isActive()) {
                throw new IllegalStateException("Transaction " + txnId + " is not yet started");
            }
            if (getRollbackOnly()) {
                throw new IllegalStateException("Transaction " + txnId + " is already marked as rollback-only");
            }
            // Send any in-transit entities to DB, before issuing a commit.
            work.entityManager().flush();
            work.connection().commit();
            started = false;
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Transaction [{}] is committed", txnId);
            }
        }
        catch (SQLException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Error committing transaction [" + txnId + "]", e);
            }
            throw new JdbcException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            if (!isActive()) {
                throw new IllegalStateException("Transaction " + txnId + " is not yet started");
            }
            work.connection().rollback();
            started = false;
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Transaction [{}] is rolled back", txnId);
            }
        }
        catch (SQLException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Error rolling back transaction [" + txnId + "]", e);
            }
            throw new JdbcException(e);
        }
    }

    @Override
    public void setRollbackOnly() {
        rollbackOnly = true;
    }

    @Override
    public boolean getRollbackOnly() {
        return rollbackOnly;
    }

    @Override
    public boolean isActive() {
        return started;
    }

    @Override
    public void setTimeout(Integer intgr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getTimeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
