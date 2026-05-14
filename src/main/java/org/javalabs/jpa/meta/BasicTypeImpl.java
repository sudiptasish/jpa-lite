package org.javalabs.jpa.meta;

import jakarta.persistence.metamodel.BasicType;
import jakarta.persistence.metamodel.Type.PersistenceType;

/**
 * Implementation of a basic (non-entity) type in the persistence metamodel.
 * 
 * <p>
 * Represents simple attribute types such as primitives, wrappers,
 * and standard Java types.
 *
 * @author Sudiptasish Chanda
 */
public class BasicTypeImpl<T> extends TypeImpl<T> implements BasicType<T> {
    
    public BasicTypeImpl(PersistenceType persistType, Class<T> javaType) {
        super(persistType, javaType);
    }
    
}
