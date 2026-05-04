package org.javalabs.jpa.ds;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author schan280
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
