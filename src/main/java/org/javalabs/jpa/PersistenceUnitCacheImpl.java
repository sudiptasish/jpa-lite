package org.javalabs.jpa;

import jakarta.persistence.spi.PersistenceUnitInfo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Concrete class that represents a cache.
 *
 * @author Sudiptasish Chanda
 */
public class PersistenceUnitCacheImpl implements PersistenceUnitCache {
    
    private final ConcurrentMap<String, PersistenceUnitInfo> puMapping = new ConcurrentHashMap<>();

    @Override
    public void store(PersistenceUnitInfo unitInfo) {
        puMapping.put(unitInfo.getPersistenceUnitName(), unitInfo);
    }

    @Override
    public PersistenceUnitInfo get(String puName) {
        return puMapping.get(puName);
    }

    @Override
    public boolean isLoaded(String puName) {
        return puMapping.containsKey(puName);
    }
    
}
