package org.javalabs.jpa.meta;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that represents a concrete managed type.
 * 
 * <p>
 * This class represents an entity, mapped superclass and embeddable types. If your
 * application has any of these three types defined, then the meta definition of such
 * types will be explained by <code>ManagedTypeImpl</code>.
 * An application can have multiple of such types. Each type will have zero or more
 * {@link Attribute}s.
 *
 * @author Sudiptasish Chanda
 */
public abstract class ManagedTypeImpl<X> extends TypeImpl<X> implements ManagedType<X> {
    
    private final Map<String, AttributeImpl<? super X, ?>> members = new LinkedHashMap<>();
    
    protected ManagedTypeImpl(PersistenceType persistType, Class<X> javaType) {
        super(persistType, javaType);
    }
    
    protected void init() {
        // Now start extracting the column name(s).
        Field[] fields = getJavaType().getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            
            if (field.isSynthetic()
                || Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                
                continue;   // Fields of an jpa entity must be non static and non final.
            }
            AttributeImpl attribute = new SingularAttributeImpl(this, field);
            members.put(field.getName(), attribute);
        }
    }

    @Override
    public Set<Attribute<? super X, ?>> getAttributes() {
        LinkedHashSet<Attribute<? super X, ?>> set = new LinkedHashSet<>();
        set.addAll(members.values());
        return set;
    }

    @Override
    public Set<Attribute<X, ?>> getDeclaredAttributes() {
        LinkedHashSet<Attribute<X, ?>> set = new LinkedHashSet<>();
        for (AttributeImpl<? super X, ?> attr : members.values()) {
            if (attr.isDeclared()) {
                set.add((Attribute<X, ?>)attr);
            }
        }
        return set;
    }

    @Override
    public Attribute<? super X, ?> getAttribute(String name) {
        AttributeImpl<? super X, ?> attribute = members.get(name);
        if (attribute == null) {
            throw new IllegalArgumentException("No attribute found for name: " + name);
        }
        return attribute;
    }

    @Override
    public Attribute<X, ?> getDeclaredAttribute(String name) {
        AttributeImpl<X, ?> attribute = (AttributeImpl<X, ?>)getAttribute(name);
        if (!attribute.isDeclared()) {
            throw new IllegalArgumentException("No declared attribute found for name: " + name);
        }
        return attribute;
    }

    @Override
    public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
        Attribute<? super X, ?> attribute = getAttribute(name);
        return (SingularAttribute<? super X, ?>)attribute;
    }

    @Override
    public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
        Attribute<X, ?> attribute = getDeclaredAttribute(name);
        return (SingularAttribute<X, ?>)attribute;
    }

    @Override
    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
        SingularAttribute<? super X, Y> attribute = (SingularAttribute<? super X, Y>)getSingularAttribute(name);
        if (attribute.getType().getJavaType() != type) {
            throw new IllegalArgumentException("Attribute " + name + " is not of type " + type.getSimpleName());
        }
        return attribute;
    }

    @Override
    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type) {
        SingularAttribute<X, Y> attribute = (SingularAttribute<X, Y>)getDeclaredSingularAttribute(name);
        if (attribute.getType().getJavaType() != type) {
            throw new IllegalArgumentException("Declared Attribute " + name + " is not of type " + type.getSimpleName());
        }
        return attribute;
    }

    @Override
    public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
        LinkedHashSet<SingularAttribute<? super X, ?>> set = new LinkedHashSet<>();
        for (AttributeImpl<? super X, ?> attr : members.values()) {
            if (attr instanceof SingularAttribute) {
                set.add((SingularAttribute<? super X, ?>)attr);
            }
        }
        return set;
    }

    @Override
    public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
        LinkedHashSet<SingularAttribute<X, ?>> set = new LinkedHashSet<>();
        for (AttributeImpl<? super X, ?> attr : members.values()) {
            if (attr.isDeclared() && attr instanceof SingularAttribute) {
                set.add((SingularAttribute<X, ?>)attr);
            }
        }
        return set;
    }

    @Override
    public CollectionAttribute<? super X, ?> getCollection(String name) {
        Attribute<? super X, ?> attribute = getAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a collection type");
        }
        return (CollectionAttribute<? super X, ?>)attribute;
    }

    @Override
    public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
        AttributeImpl<X, ?> attribute = (AttributeImpl<X, ?>)getDeclaredAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a collection type");
        }
        return (CollectionAttribute<X, ?>)attribute;
    }

    @Override
    public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
        CollectionAttribute<? super X, E> attribute = (CollectionAttribute<? super X, E>)getCollection(name);
        if (attribute.getJavaType() != elementType) {
            throw new IllegalArgumentException("Collection attribute " + name + " is not of type " + elementType);
        }
        return attribute;
    }

    @Override
    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
        CollectionAttribute<X, E> attribute = (CollectionAttribute<X, E>)getDeclaredCollection(name);
        if (attribute.getJavaType() != elementType) {
            throw new IllegalArgumentException("Collection attribute " + name + " is not of type " + elementType);
        }
        return attribute;
    }

    @Override
    public SetAttribute<? super X, ?> getSet(String name) {
        Attribute<? super X, ?> attribute = getAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a set type");
        }
        return (SetAttribute<? super X, ?>)attribute;
    }

    @Override
    public SetAttribute<X, ?> getDeclaredSet(String name) {
        Attribute<X, ?> attribute = getDeclaredAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a set type");
        }
        return (SetAttribute<X, ?>)attribute;
    }

    @Override
    public <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
        SetAttribute<? super X, E> attribute = (SetAttribute<? super X, E>)getSet(name);
        if (attribute.getJavaType() != elementType) {
            throw new IllegalArgumentException("Set attribute " + name + " is not of type " + elementType);
        }
        return attribute;
    }

    @Override
    public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
        SetAttribute<X, E> attribute = (SetAttribute<X, E>)getDeclaredSet(name);
        if (attribute.getJavaType() != elementType) {
            throw new IllegalArgumentException("Set attribute " + name + " is not of type " + elementType);
        }
        return attribute;
    }

    @Override
    public ListAttribute<? super X, ?> getList(String name) {
        Attribute<? super X, ?> attribute = getAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a list type");
        }
        return (ListAttribute<? super X, ?>)attribute;
    }

    @Override
    public ListAttribute<X, ?> getDeclaredList(String name) {
        Attribute<X, ?> attribute = getDeclaredAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a list type");
        }
        return (ListAttribute<X, ?>)attribute;
    }

    @Override
    public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
        ListAttribute<? super X, E> attribute = (ListAttribute<? super X, E>)getList(name);
        if (attribute.getJavaType() != elementType) {
            throw new IllegalArgumentException("List attribute " + name + " is not of type " + elementType);
        }
        return attribute;
    }

    @Override
    public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
        ListAttribute<X, E> attribute = (ListAttribute<X, E>)getDeclaredList(name);
        if (attribute.getJavaType() != elementType) {
            throw new IllegalArgumentException("List attribute " + name + " is not of type " + elementType);
        }
        return attribute;
    }

    @Override
    public MapAttribute<? super X, ?, ?> getMap(String name) {
        Attribute<? super X, ?> attribute = getAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a map type");
        }
        return (MapAttribute<? super X, ?, ?>)attribute;
    }

    @Override
    public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
        Attribute<X, ?> attribute = getDeclaredAttribute(name);
        if (attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            throw new IllegalArgumentException("Attribute " + name + " is not a map type");
        }
        return (MapAttribute<X, ?, ?>)attribute;
    }

    @Override
    public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        MapAttribute<? super X, K, V> attribute = (MapAttribute<? super X, K, V>)getMap(name);
        if (attribute.getElementType().getJavaType() != valueType) {
            throw new IllegalArgumentException("Map attribute " + name + " is not of type " + valueType);
        }
        return attribute;
    }

    @Override
    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
        MapAttribute<X, K, V> attribute = (MapAttribute<X, K, V>)getDeclaredMap(name);
        if (attribute.getElementType().getJavaType() != valueType) {
            throw new IllegalArgumentException("Map attribute " + name + " is not of type " + valueType);
        }
        return attribute;
    }

    @Override
    public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
        LinkedHashSet<PluralAttribute<? super X, ?, ?>> set = new LinkedHashSet<>();
        for (AttributeImpl<? super X, ?> attr : members.values()) {
            if (attr instanceof PluralAttribute) {
                set.add((PluralAttribute<? super X, ?, ?>)attr);
            }
        }
        return set;
    }

    @Override
    public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
        LinkedHashSet<PluralAttribute<X, ?, ?>> set = new LinkedHashSet<>();
        for (AttributeImpl<? super X, ?> attr : members.values()) {
            if (attr.isDeclared() && attr instanceof PluralAttribute) {
                set.add((PluralAttribute<X, ?, ?>)attr);
            }
        }
        return set;
    }
    
}
