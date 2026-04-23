package org.javalabs.jpa.query;

import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.descriptor.RelAttribute;
import java.util.List;

/**
 * Abstract binder class used to bind the result set with appropriate entity.
 *
 * @author Sudiptasish Chanda
 */
public abstract class Binder {
    
    protected PersistenceHandler handler = PersistenceHandler.get();
    
    private final RelAttribute.RelType relType;
    
    protected Binder(RelAttribute.RelType relType) {
        this.relType = relType;
    }

    public RelAttribute.RelType getRelType() {
        return relType;
    }
    
    /**
     * 
     * @param <T>
     * @param parents
     * @param rel
     * @param children
     * @return 
     */
    public abstract <T> List<T> bind(List<T> parents
        , RelAttribute rel
        , List<Object> children);
}
