package org.javalabs.jpa.query;

import org.javalabs.jpa.descriptor.RelAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One-to-many Binder class.
 * 
 * <p>
 * In a one-to-one relationship, one record in a table is associated with one or
 * more record in another table. In systems analysis, a one-to-many relationship
 * is a type of cardinality that refers to the relationship between two entities
 * A and B in which an element of A may be linked to many elements of B, but a
 * member of B is linked to only one element of A.
 * 
 * <p>
 * JPA-LiTE supports {@link RelAttribute.RelType.OneToMaany} query mapping. In which
 * case, it is expected to get one or more records (or zero in case of left outer
 * join) from the child table. This binder class will then associate the child
 * records (if found) with the parent entry.
 *
 * @author Sudiptasish Chanda
 */
public class OneToManyBinder extends Binder {
    
    public OneToManyBinder(RelAttribute.RelType relType) {
        super(relType);
    }

    @Override
    public <T> List<T> bind(List<T> parents
        , RelAttribute rel
        , List<Object> children) {
        
        Map<Object, T> tmp = new HashMap<>();
        Object jcVal = null;
        String key = "";
        int i = 0;
        
        RelAttribute childRel = handler.getDescriptor(children.get(0))
                .relation(RelAttribute.RelType.ManyToOne, rel.relation().mappedBy());
            
        for (T parent : parents) {
            i = 0;
            key = "";
            for (RelAttribute.Join join : childRel.joins()) {
                jcVal = handler.get(parent, join.referencedColumn());
                if (jcVal == null) {
                    throw new IllegalStateException(String.format(
                        "Value of join column [%s] in entity [%s] is null"
                        , join.joinColumn()
                        , parent.getClass().getSimpleName()));
                }
                key += jcVal.toString();
                
                if (i < childRel.joins().size() - 1) {
                    key += "~";
                }
                i ++;
            }
            if (! tmp.containsKey(key)) {
                tmp.put(key, parent);
            }
        }
        Object prevKey = null;
        List<Object> mappedElements = new ArrayList<>();
        T parent = null;
        
        for (Object child : children) {
            i = 0;
            key = "";
            for (RelAttribute.Join join : childRel.joins()) {
                jcVal = handler.get(child, join.joinColumn());
                if (jcVal == null) {
                    key = null;
                    break;
                }
                key += jcVal.toString();
                
                if (i < childRel.joins().size() - 1) {
                    key += "~";
                }
                i ++;
            }
            if (key != null) {
                if (prevKey != null && ! prevKey.equals(key)) {
                    parent = tmp.get(prevKey);
                    handler.set(parent, rel.name(), mappedElements);
                    mappedElements = new ArrayList<>();
                }
                mappedElements.add(child);
                prevKey = key;
            }
        }
        // Remaining one
        parent = tmp.get(prevKey);
        handler.set(parent, rel.name(), mappedElements);
        return new ArrayList<>(tmp.values());
    }
    
    private String key(RelAttribute rel) {
        int i = 0;
        String key = "";
        for (RelAttribute.Join join : rel.joins()) {
            key += join.referencedColumn();         // Remember: Uni-Directional
            if (i < rel.joins().size() - 1) {
                key += "~";
            }
            i ++;
        }
        return key;
    }
}
