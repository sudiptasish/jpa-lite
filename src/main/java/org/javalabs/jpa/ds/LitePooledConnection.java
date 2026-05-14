package org.javalabs.jpa.ds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

/**
 * Represents a lightweight wrapper around a physical database connection
 * managed within a connection pool.
 * 
 * <p>
 * This class typically maintains metadata about the connection, such as its
 * current state, usage status, and life cycle events. It may also provide hooks
 * for tracking usage and handling connection recycling.
 * <p>
 * Intended to be used internally by connection pooling implementations.
 *
 * @author Sudiptasish Chanda
 */
public class LitePooledConnection implements PooledConnection {

    private final String name;

    private final Connection actual;
    private final Connection proxy;

    private final ConnectionMetrics metrics;
    private Boolean beginRequest = Boolean.FALSE;

    private final List<ConnectionEventListener> connListeners = new ArrayList<>();
    private final List<StatementEventListener> stmtListeners = new ArrayList<>();

    public LitePooledConnection(final String name, final Connection actual) {
        this.name = name;
        this.actual = actual;
        this.metrics = new ConnectionMetrics(name);
        LiteMetricsStore.get().addMetrics(metrics);

        proxy = (Connection) Proxy.newProxyInstance(actual.getClass().getClassLoader(),
                 new Class[]{Connection.class},
                 new ConnectionDelegate(this));
    }

    public String getName() {
        return name;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(actual, args);
        }
        catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof SQLException) {
                fireConnectionError((SQLException) e.getTargetException());     // Tell listeners about exception if it's fatal
            }
            throw e.getTargetException();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!beginRequest) {
            // This method will be called by the client code just before starting to work on the underlying connection.
            // Therefore the idle time will end here.
            metrics.startCapture();
            beginRequest = Boolean.TRUE;
        }
        return this.proxy;
    }

    public Boolean isValid(Integer timeoutSecond) throws SQLException {
        return proxy.isValid(timeoutSecond);
    }

    @Override
    public void close() throws SQLException {
        fireConnectionCloseEvent();
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        connListeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        connListeners.remove(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        stmtListeners.add(listener);
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        stmtListeners.remove(listener);
    }
    
    private void fireConnectionError(SQLException e) {
        for (ConnectionEventListener listener : connListeners) {
            listener.connectionErrorOccurred(new ConnectionEvent(this, e));
        }
    }

    private void fireConnectionCloseEvent() {
        for (ConnectionEventListener listener : connListeners) {
            listener.connectionClosed(new ConnectionEvent(this));
        }
    }

    void calculateMetrics() {
        metrics.endCapture();
        beginRequest = Boolean.FALSE;
    }

    public ConnectionMetrics metrics() {
        return metrics;
    }
}
