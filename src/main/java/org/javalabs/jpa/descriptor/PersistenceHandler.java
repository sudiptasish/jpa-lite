package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.util.GeneralUtility;
import org.javalabs.jpa.util.ObjectCreationUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Core class of custom ORM framework.
 * 
 * This class keeps the metadata about individual JPA entity. It honors all
 * existing annotations of an entity, except "Join", which is the sole reason as to
 * why this framework is ultra light. It avoids creating any complex object graph
 * in the memory and does not maintain any state either.
 *
 * @author Sudiptasish Chanda
 */
public class PersistenceHandler {
    
    private static final PersistenceHandler HANDLER = new PersistenceHandler();
    
    private final ConcurrentMap<Class<?>, ClassDescriptor> entityMapping;

    private PersistenceHandler() {
        entityMapping = new ConcurrentHashMap<>();
    }
    
    /**
     * Return the single persistence handler object.
     * @return PersistenceHandler
     */
    public static PersistenceHandler get() {
        return HANDLER;
    }
    
    /**
     * Convenient method to invoke the getter method of the entity.
     * If the specified object is null or no setter method found, then this method returns
     * null, indicating the method invocation failed.
     * 
     * @param 	entity          Object whose setter method to be invoked
     * @param 	field           Field name whose value to be retrieved.
     * @return  Object          Value of the field
     */
    public Object get(Object entity, String field) {
        if (entity == null) {
            return null;
        }        
        ClassDescriptor desc = getDescriptor(entity);
        Method method = desc.getter(field);

        if (method != null) {
            return internalInvoke(entity, method, new Object[] {});
        }
        return null;
    }

    /**
     * Convenient method to invoke the setter method of the specified object.
     * If the specified object is null or no setter method found, then this method returns
     * false, indicating the method invocation failed.
     * 
     * @param 	entity          Object whose setter method to be invoked
     * @param 	field           Field name whose value is to be poppulated with 'value'.
     * @param   value           Value to be set in this field
     * @return  boolea          True, means the successful invocation of setter API, False otherwise
     */
    public boolean set(Object entity, String field, Object value) {
        if (entity == null) {
            return false;
        }        
        ClassDescriptor desc = getDescriptor(entity);
        Method method = desc.setter(field);

        if (method != null) {
            internalInvoke(entity, method, new Object[] {value});
            return true;
        }
        return false;
    }
    
    /**
     * Convenient method to invoke the getter method of the primary key.
     * Primary key of a jpa entity is ordinary pojo with no db mapping present.
     * Hence the method details are stored against the java field name.
     * 
     * @param   desc        Class descriptor.
     * @param 	pk          Primary key whose getter method to be invoked
     * @param 	field       Field name whose value to be retrieved.
     * @return  Object      Value of the field
     */
    public Object pkGet(ClassDescriptor desc, Object pk, String field) {
        Method method = desc.pkGetter(field);

        if (method != null) {
            return internalInvoke(pk, method, new Object[] {});
        }
        return null;
    }

    /**
     * Here is where the actual invocation takes place
     * 
     * @param 	entity 		Object whose method is to be invoked
     * @param 	method		Method name
     * @param 	methodArgs	Method arguments
     */
    private static Object internalInvoke(Object entity
        , Method method
        , Object[] methodArgs) {
        
        try {
            return method.invoke(entity, methodArgs);
        }
        catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Copy all the property values from 'from' object to 'to' object.
     * @param from  Object the attributes will be copied from.
     * @param to    Object where the copied attributes will be placed.
     */
    public void copy(Object from, Object to) {
        if (from.getClass() != to.getClass()) {
            throw new IllegalArgumentException("From and To class does not match");
        }
        ClassDescriptor desc = getDescriptor(from);
        if (desc == null) {
            return;
        }
        for (Iterator<EntityAttribute> itr = desc.attributes(); itr.hasNext(); ) {
            EntityAttribute attribute = itr.next();
            Object val = get(from, attribute.column());
            set(to, attribute.column(), val);
        }
    }
    
    /**
     * Extract and populate the primary key object for this entity object.
     * 
     * @param entity        Entity whose primary key to be extracted
     * @return Object       Primary key object (typically the IdClass).
     * 
     */
    public Object extractPrimaryKey(Object entity) {
        ClassDescriptor desc = getDescriptor(entity);
        if (desc == null) {
            return null;
        }
        // If Primary key is a primitive type ....
        if (GeneralUtility.isPrimitive(desc.primaryKey())) {
            EntityAttribute attribute = desc.ids().next();
            return get(entity, attribute.column());
        }
        // Primary key is a POJO.
        // Therefore ensure all the java fields of this key object are populated.
        Object primaryKey = ObjectCreationUtil.create(desc.primaryKey().getName());
        for (Iterator<EntityAttribute> itr = desc.ids(); itr.hasNext(); ) {
            EntityAttribute attribute = itr.next();
            Object val = get(entity, attribute.column());
            //TODO(This set method throws null pointer exception as the primaryKey object sent is not a valid JPA entity.
            // This has to be revisited.)
            set(primaryKey, attribute.name(), val);
        }
        return primaryKey;
    }
    
    /**
     * Return the entity class metadata.
     * @param entity
     * @return EntityClass
     */
    public ClassDescriptor getDescriptor(Object entity) {
        if (entity == null) {
            return null;
        }        
        return getDescriptor(entity.getClass());
    }
    
    /**
     * Return the entity class metadata.
     * @param clazz
     * @return EntityClass
     */
    public ClassDescriptor getDescriptor(Class<?> clazz) {
        return entityMapping.get(clazz);
    }
    
    /**
     * Create the descriptor for new entity class.
     * @param clazz 
     */
    public void createDescriptor(Class<?> clazz) {
        if (! entityMapping.containsKey(clazz)) {
            entityMapping.put(clazz, new EntityClassImpl(clazz));
        }
    }
    
    /**
     * Return and iterator over the list of column names defined for this entity.
     * @param entity
     * @return Iterator
     */
    public Iterator<EntityAttribute> columns(Object entity) {
        ClassDescriptor entityClass = entityMapping.get(entity.getClass());        
        return entityClass.attributes();
    }
    
    /**
     * Return the list of jpa entities.
     * @return List
     */
    public List<Class<?>> entities() {
        return new ArrayList<>(entityMapping.keySet());
    }
}
