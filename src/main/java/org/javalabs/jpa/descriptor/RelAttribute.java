package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.annotation.Inner;
import org.javalabs.jpa.annotation.LeftOuter;
import org.javalabs.jpa.annotation.RightOuter;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

/**
 * This class represents the metadata of a relational attribute present in an entity.
 * 
 * <p>
 * A jpa entity can have zero or more relational attribute(s). Often they are broadly
 * categorized into:
 * <ul>
 *   <li> {@link OneToOne} </li>
 *   <li> {@link OneToMany} </li>
 *   <li> {@link ManyToOne} </li>
 *   <li> {@link ManyToMany} </li>
 * </ul>
 * 
 * If an entity has one of the attributes present, then the metadata about such
 * relationship will be stored in this class.
 *
 * @author Sudiptasish Chanda
 */
public interface RelAttribute extends EntityAttribute {
    
    enum RelType {
        OneToOne,
        OneToMany,
        ManyToMany,
        ManyToOne
    };
    
    enum JoinType {
        INNER ("INNER JOIN"),
        LEFT_OUTER ("LEFT OUTER JOIN"),
        RIGHT_OUTER ("RIGHT OUTER JOIN"),
        FULL_OUTER ("FULL OUTER JOIN");
        
        private final String syntax;
        
        JoinType(String syntax) {
            this.syntax = syntax;
        }
        
        String syntax() {
            return this.syntax;
        }
    };
    
    /**
     * Return the parent-child relationship.
     * @return Relation
     */
    Relation relation();
    
    /**
     * Return the join(s).
     * @return List
     */
    List<Join> joins();
    
    /**
     * Return the join type.
     * Join type is one of {@link Inner}, {@link LeftOuter} or {@link RightOuter}.
     * 
     * @return String
     */
    JoinType joinType();
    
    /**
     * This class represents the relationship between two entities.
     * 
     * <p>
     * Relationship is always one-way. Following relationships are supported:
     * <ui>
     *   <li>OneToOne</li>
     *   <li>OneToMany</li>
     *   <li>ManyToOne</li>
     *   <li>ManyToMany</li>
     * </ul>
     * 
     * Although the relationship information is well captured, however, jpa-lite
     * supports only one-way (parent to child) relationship, i.e., OneToOne and OneToMany
     */
    interface Relation {
        
        RelType relType();

        CascadeType[] cascade();

        FetchType fetch();
        
        String mappedBy();
    }
    
    /**
     * Represent a {@link JoinColumn} annotation in a parent-child relationship.
     */
    interface Join {
        
        String joinColumn();

        String referencedColumn();

        boolean fk();

        String joinTable();
    }
}
