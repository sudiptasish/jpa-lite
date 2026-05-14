package org.javalabs.jpa.ds;

import java.sql.SQLException;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

/**
 * Listener interface for receiving events related to pooled connection life cycle.
 * 
 * <p>
 * Implementations of this interface can be used to handle events such as
 * connection creation, acquisition, release, validation failure, or closure.
 * <p>
 * Enables decoupled monitoring and management of connection pool behavior.
 *
 * @author Sudiptasish Chanda
 */
public class PooledConnectionEventListener implements ConnectionEventListener {

    private final EternalDataSource datasource;

    public PooledConnectionEventListener(EternalDataSource datasource) {
        this.datasource = datasource;
    }

    public EternalDataSource getDatasource() {
        return datasource;
    }

    @Override
    public void connectionClosed(ConnectionEvent event) {
        // The event has been received as the application has just called java.sql.Connection.close().
        // the connection has to be returned to the pool
        LitePooledConnection pooledConn = (LitePooledConnection) event.getSource();
        pooledConn.calculateMetrics();

        datasource.closePooledConnection(pooledConn);
    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent event) {
        try {
            LitePooledConnection pooledConn = (LitePooledConnection) event.getSource();

            SQLException e = event.getSQLException();
            if (isFatal(e.getSQLState())) {
                pooledConn.metrics().setStatus("INVALID");      // Prepare for eviction
                pooledConn.close();
            }
        }
        catch (SQLException e) {
            // Do Nothing
        }
    }
    

    private Boolean isFatal(String sqlState) {
        if (sqlState == null || sqlState.length() < 2) {
            return Boolean.TRUE;            // No (class) info, assume fatal
        }
        for (String fatalClass : getDatasource().getConfig().getFatalClasses()) {
            if (sqlState.startsWith(fatalClass)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
