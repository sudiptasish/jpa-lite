package org.javalabs.jpa.meta;

import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementation of metadata model class.
 *
 * @author Sudiptasish Chanda
 */
public class MetadataModelImpl implements Metamodel {
    
    private final LinkedHashMap<String, EntityType<?>> map = new LinkedHashMap<>();

    @Override
    public <X> EntityType<X> entity(Class<X> cls) {
        return (EntityType<X>)map.get(cls.getName());
    }

    @Override
    public Set<EntityType<?>> getEntities() {
        return new LinkedHashSet(map.values());
    }
    
    public <X> void addEntityType(EntityType<X> entityType) {
        map.put(entityType.getName(), entityType);
    }

    @Override
    public <X> ManagedType<X> managedType(Class<X> cls) {
        return (ManagedType<X>) map.get(cls.getName());
    }

    @Override
    public <X> EmbeddableType<X> embeddable(Class<X> cls) {
        return (EmbeddableType<X>) map.get(cls.getName());
    }

    @Override
    public Set<ManagedType<?>> getManagedTypes() {
        LinkedHashSet<ManagedType<?>> managedTypes = new LinkedHashSet<>();
        for (EntityType<?> type : map.values()) {
            if (ManagedType.class.isAssignableFrom(type.getClass())) {
                managedTypes.add((ManagedType<?>)type);
            }
        }
        return managedTypes;
    }

    @Override
    public Set<EmbeddableType<?>> getEmbeddables() {
        LinkedHashSet<EmbeddableType<?>> managedTypes = new LinkedHashSet<>();
        for (EntityType<?> type : map.values()) {
            if (EmbeddableType.class.isAssignableFrom(type.getClass())) {
                managedTypes.add((EmbeddableType<?>)type);
            }
        }
        return managedTypes;
    }

    @Override
    public EntityType<?> entity(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
