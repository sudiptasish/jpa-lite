package org.javalabs.jpa.meta;

import jakarta.persistence.metamodel.EntityType;

/**
 *
 * @author Sudiptasish Chanda
 */
public class EntityMetaExtractor extends MDExtractor {
    
    EntityMetaExtractor() {}

    @Override
    public <X> EntityType<X> extract(String entity) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            Class<?> clazz = loader.loadClass(entity);
            EntityTypeImpl<?> type = new EntityTypeImpl(clazz);
            type.init();
            
            return (EntityType<X>)type;
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
