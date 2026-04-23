package org.javalabs.jpa.query;

import org.javalabs.jpa.descriptor.RelAttribute;
import java.util.List;

/**
 * One-to-one Binder class.
 * 
 * <p>
 * In a one-to-one relationship, one record in a table is associated with one and
 * only one record in another table. A a one-to-one relationship is a type of 
 * cardinality that refers to the relationship between two entities A and B in 
 * which one element of A may only be linked to one element of B, and vice versa.
 * 
 * <p>
 * JPA-LiTE supports {@link RelAttribute.RelType.OneToOne} query mapping. In which
 * case, it is expected to get exactly one record (or zero in case of left outer
 * join) from the child table. This binder class will then associate the child
 * record (if found) with the parent entry.
 *
 * @author Sudiptasish Chanda
 */
public class OneToOneBinder extends Binder {

    public OneToOneBinder(RelAttribute.RelType relType) {
        super(relType);
    }

    @Override
    public <T> List<T> bind(List<T> parents
        , RelAttribute rel
        , List<Object> children) {
        
        Object parent = null;
        Object child = null;
        
        for (int i = 0; i < parents.size(); i ++) {
            parent = parents.get(i);
            child = children.get(i);
            
            handler.set(parent, rel.name(), child);
        }
        return parents;
    }
    
}
