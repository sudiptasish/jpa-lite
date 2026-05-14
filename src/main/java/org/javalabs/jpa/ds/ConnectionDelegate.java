package org.javalabs.jpa.ds;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Acts as a delegating wrapper for a database connection.
 * 
 * <p>
 * Forwards method calls to an underlying connection instance while optionally
 * adding additional behavior such as logging, metrics collection, or lifecycle
 * management.
 * <p>
 * Commonly used to extend or customize connection behavior without modifying
 * the underlying implementation.
 *
 * @author Sudiptasish Chanda
 */
public class ConnectionDelegate implements InvocationHandler {

    private static final String METHOD_CLOSE = "close";

    private final LitePooledConnection pooledConnection;

    ConnectionDelegate(LitePooledConnection pooledConnection) {
        this.pooledConnection = pooledConnection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            if ("equals".equals(method.getName())) {
                return proxy == args[0];
            }
            if ("hashCode".equals(method.getName())) {
                return System.identityHashCode(proxy);
            }
            try {
                return pooledConnection.invoke(proxy, method, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        // On the Connection interface
        if (METHOD_CLOSE.equals(method.getName())) {
            pooledConnection.close();
            return null;
        }
        return pooledConnection.invoke(proxy, method, args);
    }
}
