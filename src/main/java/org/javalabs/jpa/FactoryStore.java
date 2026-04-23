package org.javalabs.jpa;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Store to hold the actiev {@link EntityManagerFactory}.
 * 
 * <p>
 * {@link EntityManagerFactory} is a singleton instance. Once the persistence config
 * file has been read and processed, the instance of entity manager factory is
 * created and keep it in memory. So that aa subsequent call can return the same
 * factory class from the store.
 *
 * @author Sudiptasish Chanda
 */
public final class FactoryStore {
    
    private static final FactoryStore STORE = new FactoryStore();
    
    private final Map<String, EntityManagerFactory> factoryMap = new HashMap<>();
    
    private FactoryStore() {}
    
    /**
     * Return the factory store.
     * @return FactoryStore
     */
    public static FactoryStore getInstance() {
        return STORE;
    }
    
    /**
     * Add this {@link EntityManagerFactory} to the store.
     * 
     * @param puName    Persistence unit name
     * @param factory   Entity manager factory for this unit.
     */
    public void addFactory(String puName, EntityManagerFactory factory) {
        factoryMap.put(puName, factory);
    }
    
    /**
     * Return the {@link EntityManagerFactory} for this persistence unit name.
     * 
     * @param puName    Persistence unit name
     * @return EntityManagerFactory
     */
    public EntityManagerFactory getFactory(String puName) {
        return factoryMap.get(puName);
    }
    
    /**
     * Right now multiple persistence unit is not supported. Therefore it is
     * expected to have a single {@link EntityManagerFactory} instance across the
     * application.
     * 
     * @return EntityManagerFactory
     */
    public EntityManagerFactory getDefault() {
        Iterator<EntityManagerFactory> itr = factoryMap.values().iterator();
        if (itr.hasNext()) {
            return itr.next();
        }
        return null;
    }
}
