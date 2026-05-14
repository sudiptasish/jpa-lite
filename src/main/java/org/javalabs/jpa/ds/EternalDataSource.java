package org.javalabs.jpa.ds;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

/**
 * Internal data source.
 *
 * @author Sudiptasish Chanda
 */
public class EternalDataSource implements ConnectionPoolDataSource {
    
    private static final Boolean IDLE = Boolean.FALSE;
    private static final Boolean BUSY = Boolean.TRUE;
    
    private final AtomicBoolean lock = new AtomicBoolean(IDLE);
    private final AtomicInteger counter = new AtomicInteger(0);
    
    private final LiteDataSourceConfig config;
    private final LiteConnectionPool pool;
    
    private PrintWriter out;
    
    public EternalDataSource(Map<String, Object> config) {
        this.config = new LiteDataSourceConfig(config);
        this.pool = new LiteConnectionPool();
        
        init();
    }
    
    public void init() {
        try {
            for (int i = 0; i < this.config.getCurrentSize(); i ++) {
                this.pool.add(newConnection());
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            
            LitePooledConnection connection = pool.borrowFromPool(config.getConnTimeout());
            if (connection == null) {
                if (config.getCurrentSize() < config.getMaxSize()) {
                    // Still have room for more connection. Add more connection ...
                    pool.add(newConnection());
                    config.incrementCurrentSize();

                    connection = pool.borrowFromPool(config.getConnTimeout());
                }
                else {
                    throw new SQLException("Connection pool exhausted: Timeout waiting for connection");
                }
            }
            if (config.getVerify()) {
                if (! connection.isValid(5)) {
                    pool.evictFromPool(connection);
                }
            }
            return connection;
        }
        catch (InterruptedException e) {
            throw new SQLException(e);
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        return newConnection(user, password);
    }
    
    public void closePooledConnection(LitePooledConnection connection) {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            if (connection.metrics().getStatus().equals("INVALID")) {
                pool.evictFromPool(connection);
            }
            else {
                pool.returnToPool(connection);
            }
        }
        finally {
            lock.set(IDLE);
        }
    }
    
    private LitePooledConnection newConnection() throws SQLException {
        return newConnection(config.getUser(), config.getPassword());
    }
    
    private LitePooledConnection newConnection(String user, String password) throws SQLException {
        Connection actual = DriverManager.getConnection(config.getUrl(), user, password);
        actual.setTransactionIsolation(config.getTxnIsolation());
        
        LitePooledConnection ltConn = new LitePooledConnection("lt-pooled-connection-" + counter.incrementAndGet(), actual);
        ltConn.addConnectionEventListener(new PooledConnectionEventListener(this));
        return ltConn;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.out;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.out = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        // Empty block
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        // Empty block
        return 0;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LiteDataSourceConfig getConfig() {
        return config;
    }
    
}
