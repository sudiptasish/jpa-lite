package org.javalabs.jpa.txn;

import org.javalabs.jpa.LiteEntityManager;
import org.javalabs.jpa.annotation.NotSupported;
import org.javalabs.jpa.annotation.Required;
import org.javalabs.jpa.annotation.RequiresNew;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Dynamic proxy for database connection and transaction management.
 * 
 * <p>
 * A dynamic proxy class (simply referred to as a proxy class below) is a class
 * that implements a list of interfaces specified at runtime when the class is
 * created, with behavior as described below. A proxy interface is such an interface
 * that is implemented by a proxy class. A proxy instance is an instance of a proxy
 * class. Each proxy instance has an associated invocation handler object, which
 * implements the interface InvocationHandler. A method invocation on a proxy
 * instance through one of its proxy interfaces will be dispatched to the invoke
 * method of the instance's invocation handler, passing the proxy instance,
 * a {@link Method} object identifying the method that was invoked, and an array
 * of type Object containing the arguments.
 * 
 * <p>
 * This proxy class instantiates a new proxy instance for the data access object
 * class. It will start obtain an entity manager and depending on the method
 * attribute in the DAO class, start a transaction.
 *
 * @author Sudiptasish Chanda
 */
public class TransactionalProxy extends DBProxy implements InvocationHandler {
    
    // Thread local object to hold the current thread's native entity manager.
    private static final ThreadLocal<LitePersistenceContext> EM_LOCAL = new ThreadLocal<>();
    
    // Original instance of the class whose proxy instance to be created.
    private final Object obj;
    private final TxnEntityManagerDelegate txnEm;
    private String puName;
    
    private TransactionalProxy(Object obj) {
        this.obj = obj;
        this.txnEm = new TxnEntityManagerDelegate();
        
        // Inject the transactional entity manager.
        try {
            boolean found = false;
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(PersistenceContext.class)) {
                    field.setAccessible(true);
                    field.set(obj, txnEm);
                    
                    this.puName = field.getAnnotation(PersistenceContext.class).name().trim();
                    if (this.puName.length() == 0) {
                        throw new IllegalArgumentException("No persistence unit name is present");
                    }
                    super.init(puName);
                    
                    found = true;
                    break;
                }
            }
            if (! found) {
                throw new IllegalArgumentException("No PersistenceContext annotation found."
                    + " Entity Manager cannot be injected");
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Object instance(Class clazz) {
        return ProxyStore.getInstance().get(clazz);
    }

    /**
     * Create a new proxy instance of the class as identified by this object.
     * Proxy goes by the interface definition. Therefore it is mandatory for the
     * object to have implemented one or more interfaces.
     * 
     * @param obj       Object whose proxy class to be created.
     * @return Object   New proxy instance
     */
    public static Object newInstance(Object obj) {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(),
            obj.getClass().getInterfaces(),
            new TransactionalProxy(obj)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // This is how your proxy knows what to do when a method is invoked. 
        // It is an object that implements InvocationHandler. When a method from
        // any of the supported interfaces, or hashCode, equals, or toString, is
        // invoked, the method invoke is invoked on the handler, passing the Method
        // object for the method to be invoked and the arguments passed
        
        // For hashCode(), equals() or toString() or any other methods inheriting
        // from the Object class need a different treatment.
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(obj, args);
        }
        
        LitePersistenceContext puContext = null;
        boolean required = false;
        
        try {
            // Check whether a txn is required.
            required = isTxnRequired(method);
            
            if (required) {
                // Create a new entity manager first, this is anyway needed.
                LiteEntityManager liteEm = (LiteEntityManager)getEm();
                Object owner = obj;
                
                if ((puContext = currentContext()) != null) {
                    // Create a new persistence context and set the current context
                    // as parent of new persistence context.
                    LitePersistenceContext currentCtx = new PersistenceContextImpl(liteEm, owner, puContext);
                    setCurrent(currentCtx);
                    puContext = currentCtx;
                }
                else {
                    // No active persistence context found in the current call stack,
                    // Therefore, set the new persistence context as active.
                    LitePersistenceContext currentCtx = new PersistenceContextImpl(liteEm, owner, null);
                    setCurrent(currentCtx);
                    puContext = currentCtx;
                }
                // Now refer this entity manager from proxy, and start the txn.
                txnEm.setProxy(puContext.em());
                puContext.em().getTransaction().begin();
            }
            else {
                // Create a new persistence context, only if none exists.
                // But do not start a new transaction.
                if ((puContext = currentContext()) == null) {
                    LiteEntityManager liteEm = (LiteEntityManager)getEm();
                    Object owner = obj;
                    
                    LitePersistenceContext currentCtx = new PersistenceContextImpl(liteEm, owner, null);
                    setCurrent(currentCtx);
                    puContext = currentCtx;
                }
                txnEm.setProxy(puContext.em());
            }
            Object result = method.invoke(obj, args);
            
            if (required) {
                puContext.em().getTransaction().commit();
            }
            return result;
        }
        catch (Throwable e) {
            if (required) {
                // It is possible that the persistence unit context itelf is null,
                // in case there is some connectivity issue.
                if (puContext != null) {
                    puContext.em().getTransaction().setRollbackOnly();
                    puContext.em().getTransaction().rollback();
                }
            }
            if (e instanceof InvocationTargetException) {
                throw ((InvocationTargetException)e).getTargetException();
            }
            else {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }
                else {
                    throw new RuntimeException(e);
                }
            }
        }
        finally {
            if (puContext != null && puContext.owner() == obj) {
                destroyPersistenceContext(puContext);
            }
        }
    }
    
    /**
     * Destroy the current persistence context.
     * @param puContext
     */
    private void destroyPersistenceContext(LitePersistenceContext puContext) {
        txnEm.unsetProxy();
        closeEm(puContext.em());
        puContext.destroy();
        removeCurrent();
        
        // Now check if this persistence context has a parent context set.
        // If present, then set the parent as active context.
        if (puContext.parent() != null) {
            setCurrent(puContext.parent());
        }
    }
    
    /**
     * Check the method annotation and initiate a txn (if required).
     * 
     * Here are the rules for starting a txn:
     * If the method has annotation {@link RequiresNew}, a new txn will always be started.
     * If the method has annotated {@link Required}, and no transaction is
     * currently active, then a new txn will be created.
     * If the methood has annotation {@link NotSupported}, the a txn will never be started.
     * 
     * Default: true.
     * 
     * @param method 
     */
    private boolean isTxnRequired(Method method) {
        boolean required = false;
        
        if (method.isAnnotationPresent(NotSupported.class)) {
            required = false;
        }
        else if (method.isAnnotationPresent(RequiresNew.class)) {
            required = true;
        }
        else {
            // Transaction attribute is either Required, or not present...
            if (currentContext() == null) {
                required = true;
            }
        }
        return required;
    }
    
    /**
     * Return the current persistence context.
     * @return LitePersistenceContext
     */
    private LitePersistenceContext currentContext() {
        return EM_LOCAL.get();
    }
    
    /**
     * Set the persistence context as a current transactional context.
     * @param puContext    Persistent context to set as current.
     */
    private void setCurrent(LitePersistenceContext puContext) {
        EM_LOCAL.set(puContext);
    }
    
    /**
     * Remove the current transactional context.
     */
    private void removeCurrent() {
        EM_LOCAL.remove();
    }
}
