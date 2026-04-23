package org.javalabs.jpa.meta;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.metamodel.Bindable.BindableType;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Type;
import java.util.Map;

/**
 * Platform provided implementation for {@link EntityType}.
 *
 * @author Sudiptasish Chanda
 */
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {
    
    private final String name;
    
    public EntityTypeImpl(Class<X> clazz) {
        this(Type.PersistenceType.ENTITY, clazz);
    }
    
    protected EntityTypeImpl(Type.PersistenceType persistType, Class<X> clazz) {
        super(persistType, clazz);
        this.name = clazz.getName();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public BindableType getBindableType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<X> getBindableJavaType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityGraph<X> createEntityGraph() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, EntityGraph<X>> getNamedEntityGraphs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
