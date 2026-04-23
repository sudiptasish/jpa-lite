package org.javalabs.jpa.meta;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that represents an identifiable type.
 *
 * @author Sudiptasish Chanda
 */
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {
    
    private IdentifiableType<? super X> superType;
    
    private final LinkedHashSet<SingularAttributeImpl<? super X, ?>> idSet = new LinkedHashSet<>();

    private final LinkedHashSet<SingularAttributeImpl<? super X, ?>> versionSet = new LinkedHashSet<>();

    protected IdentifiableTypeImpl(PersistenceType persistType, Class<X> javaType) {
        super(persistType, javaType);
    }
    
    @Override
    protected void init() {
        // Now extract the id and version attribute(s).
        Field[] fields = getJavaType().getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            
            if (field.isSynthetic()
                || Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                
                continue;   // Fields of an jpa entity must be non static and non final.
            }
            if (field.isAnnotationPresent(Id.class)) {
                idSet.add(new SingularAttributeImpl(this, field));
            }
            if (field.isAnnotationPresent(Version.class)) {
                versionSet.add(new SingularAttributeImpl(this, field));
            }
        }
        super.init();
    }

    @Override
    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
        for (SingularAttributeImpl<? super X, ?> attribute : idSet) {
            if (attribute.getJavaType() == type) {
                return (SingularAttribute<? super X, Y>)attribute;
            }
        }
        throw new IllegalArgumentException("No id attribute of type " + type.getSimpleName() + " found");
    }

    @Override
    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
        for (SingularAttributeImpl<? super X, ?> attribute : idSet) {
            if (attribute.getJavaType() == type && attribute.isDeclared()) {
                return (SingularAttribute<X, Y>)attribute;
            }
        }
        throw new IllegalArgumentException("No declared id attribute of type " + type.getSimpleName() + " found");
    }

    @Override
    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
        for (SingularAttributeImpl<? super X, ?> attribute : versionSet) {
            if (attribute.getJavaType() == type) {
                return (SingularAttribute<? super X, Y>)attribute;
            }
        }
        throw new IllegalArgumentException("No version attribute of type " + type.getSimpleName() + " found");
    }

    @Override
    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
        for (SingularAttributeImpl<? super X, ?> attribute : versionSet) {
            if (attribute.getJavaType() == type && attribute.isDeclared()) {
                return (SingularAttribute<X, Y>)attribute;
            }
        }
        throw new IllegalArgumentException("No version attribute of type " + type.getSimpleName() + " found");
    }

    @Override
    public IdentifiableType<? super X> getSupertype() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasSingleIdAttribute() {
        return !idSet.isEmpty();
    }

    @Override
    public boolean hasVersionAttribute() {
        return !versionSet.isEmpty();
    }

    @Override
    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
        LinkedHashSet<SingularAttribute<? super X, ?>> set = new LinkedHashSet<>();
        set.addAll(idSet);
        return set;
    }

    @Override
    public Type<?> getIdType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
