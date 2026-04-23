package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.RelAttribute.RelType;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Class to hold the metadata of a JPA entity.
 * 
 * <p>
 * At the time of initializing the managed classes, the metadata of these classes
 * are extracted and kept the in the {@link ClassDescriptor}. Later this metadata
 * is used to build various queries to facilitate communicaation with the DB.
 * This is a crucial part of framework initialization. If an entity/managed
 * class is wrongly configured, then the application bootstrap will fail. Therefore
 * utmost care must be taken while modelling the jpa entities. 
 * 
 * <p>
 * JPA-LiTE does not support all attributes of persistence framework. All sort of mapping
 * attributes, e.g., {@link OneToMany}, {@link ManyToOne}, {@link ManyToMany} are
 * ignored. This is purposely done to increase the throughput of the application.
 * Traditionaal jpa framework, e.g., Eclipselink, Hibernaate, ObjectDB, etc are slow.
 * And that's because of unnecessary join attributes. Hence using these attributes
 * is strongly discouraged.
 * 
 * @author Sudiptasish Chanda
 */
public interface ClassDescriptor {
    
    /**
     * Return the java type of the entity/managed class.
     * The identification criteria of an entity is {@link Entity} annotation. If
     * no such annotation is present, then the framework will fail to initialize.
     * 
     * @return Class
     */
    Class<?> entityClass();
    
    /**
     * Return the table name that is represented by this entity class.
     * The table definition is obtained from {@link Table} annotation.
     * 
     * @return String
     */
    String table();
    
    /**
     * Return the number of attributes this entity has.
     * @return int
     */
    int attributeCount();
    
    /**
     * Return the column count representing the primary key.
     * @return int 
     */
    int idCount();
    
    /**
     * Return the java type of the primary key.
     * Because it is advisable all tables to have a primary key, therefore, it is
     * mandatory for all entity/managed class to have a primary key attribute. It
     * can be a composite key or a single primary key. In either case the primary
     * key class must be associated with the entity class via {@link IdClass} 
     * annotation.
     * 
     * @return Class
     */
    Class<?> primaryKey();
    
    /**
     * Return the attribute object for this attribute/field name.
     * 
     * @param name  Name of the java field
     * @return EntityAttribute
     */
    EntityAttribute attribute(String name);
    
    /**
     * Check if the entity class has an attribute as identified by the column name.
     * @param name  Column name.
     * @return boolean
     */
    boolean hasAttribute(String name);
    
    /**
     * Return an iterator over the set of attributes that this entity has.
     * @return Iterator
     */
    Iterator<EntityAttribute> attributes();
    
    /**
     * Return an iterator over the set of primary key names.
     * @return Iterator
     */
    Iterator<EntityAttribute> ids();
    
    /**
     * Return the setter method corresponding to this column name.
     * @param name  Name of the table column
     * @return Method
     */
    Method setter(String name);
    
    /**
     * Return the getter method corresponding to this column name.
     * @param name  Name of the table column
     * @return Method
     */
    Method getter(String name);
    
    /**
     * Return the getter method corresponding to this field name of a primary key.
     * @param name  Name of the java key field name
     * @return Method
     */
    Method pkGetter(String name);
    
    /**
     * Return the attribute for oneToOne mapping field.
     * @return List
     */
    List<RelAttribute> oneToOne();
    
    /**
     * Return the attribute for oneToMany mapping field.
     * @return List
     */
    List<RelAttribute> oneToMany();
    
    /**
     * Return the attribute for manyToOne mapping field.
     * @return List
     */
    List<RelAttribute> manyToOne();
    
    /**
     * Return the relation for this type and attribute name.
     * @param relType
     * @param name
     * @return RelAttribute
     */
    RelAttribute relation(RelType relType, String name);
    
    /**
     * Return the index list
     * @return Index
     */
    Index[] indexes();
}
