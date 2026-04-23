package org.javalabs.jpa.meta;

import jakarta.persistence.Basic;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable.BindableType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;
import java.lang.reflect.Field;

/**
 * Concrete class that represents a single-valued attribute.
 *
 * @author Sudiptasish Chanda
 */
public class SingularAttributeImpl<X, T> extends AttributeImpl<X, T> implements SingularAttribute<X, T> {
    
    private final Type<T> type;
    
    private final boolean id;
    private final boolean version;
    private final boolean optional;
    
    private final Attribute.PersistentAttributeType pType;
    
    public SingularAttributeImpl(ManagedType<X> declaringType, Field field) {
        super(declaringType, field);
        this.id = field.isAnnotationPresent(Id.class);
        this.version = field.isAnnotationPresent(Version.class);
        
        boolean optional = false;
        Basic basic = field.getAnnotation(Basic.class);
        if (basic != null) {
            optional = basic.optional();
        }
        this.optional = optional;
        this.type = new BasicTypeImpl(Type.PersistenceType.BASIC, (Class<T>)field.getType());
        this.pType = PersistentAttributeType.BASIC;
    }

    @Override
    public boolean isId() {
        return id;
    }

    @Override
    public boolean isVersion() {
        return version;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public Type<T> getType() {
        return type;
    }

    @Override
    public BindableType getBindableType() {
        return null;
    }

    @Override
    public Class<T> getBindableJavaType() {
        return null;
    }
}
