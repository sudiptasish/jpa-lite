package org.javalabs.jpa;

import org.javalabs.jpa.meta.MetadataModelImpl;

/**
 * Holder class to keep the metadata details.
 *
 * @author Sudiptasish Chanda
 */
public class PersistenceHolder {
    
    private static final PersistenceHolder INSTANCE = new PersistenceHolder();
    
    private final PersistenceUnitCache cache = new PersistenceUnitCacheImpl();
    private final MetadataModelImpl model = new MetadataModelImpl();
    
    private PersistenceHolder() {}
    
    public static PersistenceHolder getInstance() {
        return INSTANCE;
    }

    public PersistenceUnitCache getCache() {
        return cache;
    }

    public MetadataModelImpl getModel() {
        return model;
    }
}
