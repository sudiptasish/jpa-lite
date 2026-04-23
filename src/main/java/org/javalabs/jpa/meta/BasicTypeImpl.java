package org.javalabs.jpa.meta;

import jakarta.persistence.metamodel.BasicType;
import jakarta.persistence.metamodel.Type.PersistenceType;

/**
 * Concrete class that represents a basic type.
 *
 * @author Sudiptasish Chanda
 */
public class BasicTypeImpl<T> extends TypeImpl<T> implements BasicType<T> {
    
    public BasicTypeImpl(PersistenceType persistType, Class<T> javaType) {
        super(persistType, javaType);
    }
    
}
