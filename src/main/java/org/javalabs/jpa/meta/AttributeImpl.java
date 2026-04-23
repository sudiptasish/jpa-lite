package org.javalabs.jpa.meta;

import org.javalabs.jpa.util.GeneralUtility;
import jakarta.persistence.Id;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Attribute.PersistentAttributeType;
import jakarta.persistence.metamodel.ManagedType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;

/**
 * Concrete class that represents an attribute of a jpa entity.
 * 
 * <p>
 * A JPA entity can have one of multiple attributes. Some of them could be the
 * primitive types (representing the individual table columns), some of them
 * can be {@link Id} attributes, etc. We need a meta class that defines all various
 * kind of attributes. <code>AttributeImpl</code> depicts the same.
 *
 * @author Sudiptasish Chanda
 */
public abstract class AttributeImpl<X, T> implements Attribute<X, T> {
    
    private final ManagedType<X> managedType;
    private final String name;
    private final Class<T> clazz;
    private final Member member;
    private final PersistentAttributeType attrType;
    
    private final boolean declared;
    
    protected AttributeImpl(ManagedType<X> managedType, Field field) {
        this.managedType = managedType;
        this.name = field.getName();
        this.clazz = (Class<T>)field.getType();
        this.member = field;
        
        this.attrType = GeneralUtility.isCollection(field.getType())
            ? PersistentAttributeType.ELEMENT_COLLECTION
            : PersistentAttributeType.BASIC;
        
        declared = field.getDeclaringClass() == managedType.getJavaType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PersistentAttributeType getPersistentAttributeType() {
        return attrType;
    }

    @Override
    public ManagedType<X> getDeclaringType() {
        return managedType;
    }

    @Override
    public Class<T> getJavaType() {
        return clazz;
    }

    @Override
    public Member getJavaMember() {
        return member;
    }

    @Override
    public boolean isAssociation() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    public boolean isDeclared() {
        return declared;
    }
}
