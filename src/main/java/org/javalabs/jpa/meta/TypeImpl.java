package org.javalabs.jpa.meta;

import jakarta.persistence.metamodel.Type;
import jakarta.persistence.metamodel.Type.PersistenceType;

/**
 * Base implementation of a type in the persistence metamodel.
 * 
 * <p>
 * Provides common behavior and metadata handling for different
 * type categories such as basic, entity, and embeddable types.
 *
 * @author Sudiptasish Chanda
 */
public abstract class TypeImpl<X> implements Type<X> {
    
    private final PersistenceType persistType;
    private final Class<X> javaType;
    
    protected TypeImpl(PersistenceType persistType, Class<X> javaType) {
        this.persistType = persistType;
        this.javaType = javaType;
    }

    @Override
    public PersistenceType getPersistenceType() {
        return persistType;
    }

    @Override
    public Class<X> getJavaType() {
        return javaType;
    }
}
