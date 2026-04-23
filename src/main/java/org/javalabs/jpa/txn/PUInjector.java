package org.javalabs.jpa.txn;

import org.javalabs.jpa.annotation.Dao;
import org.javalabs.jpa.util.ObjectCreationUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public class PUInjector implements Injector {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PUInjector.class);

    @Override
    public void inject(List<Class> classes) {
        long start = System.currentTimeMillis();
        
        try {
            // This map is to hold the DAO implementation objects.
            Map<Class, Object> tmp = new HashMap<>();
            
            // This list is to hold any other classes that have reference to the DAO class.
            List<Class<?>> refs = new ArrayList<>();
            
            ProxyStore store = ProxyStore.getInstance();
            
            // First, instantiates all the Dao classes and put them in ProxyStore.
            boolean isDAO = false;
            for (Class<?> clazz : classes) {
                isDAO = false;
                
                if (! clazz.isInterface()) {
                    // Concrete class. Now check if any of it's interfaces has Dao annotation
                    for (Class intf : clazz.getInterfaces()) {
                        if (intf.isAnnotationPresent(Dao.class)) {
                            Object daoImpl = ObjectCreationUtil.create(clazz.getName());
                            Object existing = tmp.put(intf, daoImpl);
                            if (existing != null) {
                                throw new IllegalArgumentException("Multiple implementation found for DAO interface: " + intf);
                            }
                            isDAO = true;
                        }
                    }
                    if (! isDAO) {
                        // Now check the remaining classes to see if any of them has 
                        // reference to any DAO class. If so, add them to the special list
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(Dao.class)) {
                                refs.add(clazz);
                                break;
                            }
                        }
                    }
                }
            }
            for (Map.Entry<Class, Object> me: tmp.entrySet()) {
                Class clazz = me.getKey();
                Object daoImpl = me.getValue();
                
                store.put(clazz, TransactionalProxy.newInstance(daoImpl));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Added dao implementation to Store. Key: {}, Value: {}", clazz, daoImpl);
                }
            }
            // Start injecting "DAOImpl" to other DAO objects.
            for (Object daoImpl: tmp.values()) {
                Field[] fields = daoImpl.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Dao.class)) {
                        field.setAccessible(true);
                        Object calledDaoImpl = tmp.get(field.getType());
                        if (calledDaoImpl == null) {
                            throw new IllegalArgumentException("No implementation found for field " + field.getName());
                        }
                        field.set(daoImpl, field.getType().cast(store.get(field.getType())));
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Injected dao impl {} into object {}"
                                , store.get(field.getType()), daoImpl);
                        }
                    }
                }
            }
            
            // Inject the DAO implementation in the other selected class.
            // We have identified the other classes that require an inject.
            // But how to instantiate them ???
            // E.g., OnboardingHandler class has reference to MetadataDAO, and it is
            // instantiated in ECMHttpServer by vert.x framework. Same might be the
            // cases for other classes. A JPA implementation can not take the decision
            // to instantiate and/or initialize other framework classes.
            
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Injector finished injecting DAOImpl in all DAO classes."
                    + " Elapsed time(ms): {}", (System.currentTimeMillis() - start));
            }
        }
        catch (IllegalAccessException e) {
            LOGGER.error("Error injecting DAO Impl", e);
            throw new RuntimeException(e);
        }
    }
    
}
