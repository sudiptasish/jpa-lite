package org.javalabs.jpa;

import jakarta.persistence.spi.PersistenceUnitInfo;

/**
 * Cache to store the {@link PersistenceUnitInfo}.
 *
 * @author Sudiptasish Chanda
 */
public interface PersistenceUnitCache {
    
    /**
     * Store the persistence unit info in the cache.
     * @param unitInfo 
     */
    void store(PersistenceUnitInfo unitInfo);
    
    /**
     * Check if this persistence unit is already loaded.
     * 
     * @param puName
     * @return boolean
     */
    boolean isLoaded(String puName);
    
    /**
     * Retrieve the persistence unit info object for this unit name.
     * 
     * @param puName    Persistence unit name.
     * @return PersistenceUnitInfo
     */
    PersistenceUnitInfo get(String puName);
}
