package org.javalabs.jpa.meta;

import jakarta.persistence.metamodel.EntityType;

/**
 * Extractor class to extract the metadata of a jpa entity.
 *
 * @author Sudiptasish Chanda
 */
public abstract class MDExtractor {
    
    protected MDExtractor() {}
    
    public static MDExtractor newExtractor() {
        return new EntityMetaExtractor();
    }
    
    /**
     * 
     * @param <X>
     * @param entity
     * @return 
     */
    public abstract <X> EntityType<X> extract(String entity);
}
