package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.RelAttribute.RelType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete class to represent a managed class/jpa entity.
 * 
 * <p>
 * A jpa entity is identified by the {@link Entity} annotation. Object of this
 * class will hold the entity metadata details. If an entity has a super class
 * identified by {@link MappedSuperclass}, then the metadata of all those
 * classes will be kept here.
 *
 * @author Sudiptasish Chanda
 */
public class EntityClassImpl implements ClassDescriptor {
    
    private Class<?> entityClass;
    private String table;

    private Class<?> primaryKey;
    
    private final NamedQueryStore namedStore = NamedQueryStoreImpl.getStore();
    
    // List to keep the primary keys.
    // Although the attributes map will anyway have all the attributes, but this
    // is just a convenient place only to keep the key attribute(s).
    private final List<EntityAttribute> ids = new ArrayList<>(2);
    
    // Map to hold the field name vs getter method name for primary key
    private final Map<String, Method> pkGetters = new HashMap<>();
    
    // Map to hold the column name to field.
    private final LinkedHashMap<String, EntityAttribute> attributes = new LinkedHashMap<>();

    // Map to hold the column name vs getter method name
    private final Map<String, Method> getters = new HashMap<>();

    // Map to hold the column name vs setter method name
    private final Map<String, Method> setters = new HashMap<>();
    
    private final LinkedHashMap<RelType, List<RelAttribute>> relationships = new LinkedHashMap<>();
    
    private Index[] indexes;
    
    /**
     * If no {@link Entity} attribute is present, then an exception will be thrown.
     * @param clazz 
     */
    EntityClassImpl(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new RuntimeException("Class " + clazz.getSimpleName() + " is not a valid jpa entity");
        }
        this.entityClass = clazz;
        
        init(this.entityClass);
    }

    /**
     * Configure the entity mapping.
     * @param clazz 
     */
    private void init(Class<?> clazz) {
        // Extract the Table attribute.
        Table tabAnnotation = clazz.getAnnotation(Table.class);
        if (tabAnnotation != null) {
            table = tabAnnotation.name().toLowerCase();
            
            // Extract the Index attribute.
            indexes = tabAnnotation.indexes();
        }
        
        // Extract the IdClass attribute.
        IdClass pkAnnotation = clazz.getAnnotation(IdClass.class);
        if (pkAnnotation != null) {
            primaryKey = pkAnnotation.value();
            processPk();
        }
        
        // Extract the native query.
        processNamedQueries(clazz);
        
        // Now start extracting all the fields that are mapped to underlying
        // database table columns.
        Field[] fields = clazz.getDeclaredFields();
        boolean isRel = false;
        RelType relType = null;
        
        for (Field field : fields) {
            if (field.isSynthetic()
                || field.getName().equals("serialVersionUID")
                || Modifier.isTransient(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isInterface(field.getModifiers())
                || Modifier.isNative(field.getModifiers())) {
                
                continue;
            }
            isRel = false;
            
            if (field.isAnnotationPresent(OneToOne.class)) {
                isRel = true;
                relType = RelType.OneToOne;
            }
            else if (field.isAnnotationPresent(OneToMany.class)) {
                isRel = true;
                relType = RelType.OneToMany;
            }
            else if (field.isAnnotationPresent(ManyToOne.class)) {
                isRel = true;
                relType = RelType.ManyToOne;
            }
            else if (field.isAnnotationPresent(ManyToMany.class)) {
                isRel = true;
                relType = RelType.ManyToMany;
            }
            
            if (isRel) {
                // The id field must not have mapping. If present, throw an error.
                if (field.isAnnotationPresent(Id.class)) {
                    throw new RuntimeException("Id field " + field.getName() + " of entity " + clazz.getSimpleName()
                            + " has " + relType + " mapping. Mapping should be present against a"
                            + " valid jpa entity, or a collection of entities."
                            + " Remove any such mapping an rebuild your application.");
                }

                // An entity field that is annotated with OneToOne or OneToMany, etc
                // cannot be a regular column. Typically they refer to another entity
                // Therefore, these attributes will not be stored in 'attributes' map.
                RelAttribute rel = new RelAttributeImpl(field);

                processAttribute(clazz, field, rel);

                // You can have multiple OneToOne or OneToMany relationships, etc.
                List<RelAttribute> rels = relationships.get(relType);
                if (rels == null) {
                    rels = new ArrayList<>(2);
                    relationships.put(relType, rels);
                }
                rels.add(rel);
            }
            else {
                // If the attribute is of type collection
                EntityAttribute attribute = new EntityAttributeImpl(field);
                if (attribute.isId()) {
                    ids.add(attribute);
                }

                // Generate column mappping
                attributes.put(attribute.column(), attribute);

                processAttribute(clazz, field, attribute);
            }
        }
        // Recursively capture the attributes and other metadata of mapped super class.
        Class<?> sc = clazz.getSuperclass();
        if (sc.isAnnotationPresent(MappedSuperclass.class)) {
            init(sc);
        }
    }
    
    /**
     * Process the primary key attribute(s).
     * 
     * Although the primary key attributes are stored along with regular column
     * metadata, but a separate store is maintained to store the method mapping
     * for a primary key object.
     */
    private void processPk() {
        Field[] pkFields = primaryKey.getDeclaredFields();
        for (Field field : pkFields) {
            String name = field.getName();
            
            if (field.isSynthetic()
                || name.equals("serialVersionUID")
                || Modifier.isTransient(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isInterface(field.getModifiers())
                || Modifier.isNative(field.getModifiers())) {
                
                continue;
            }
            try {
                // Generate Getter method mapping
                Method getterMethod = primaryKey.getDeclaredMethod("get"
                        + Character.toUpperCase(name.charAt(0))
                        + name.substring(1), new Class[]{});

                pkGetters.put(field.getName(), getterMethod);
            }
            catch (NoSuchMethodException | SecurityException e) {
               // do nothing, there could be exceptions at each field level if
               // getters or setters are not defined or access to those methods
               // is restricted.
            }
        }
    }
    
    /**
     * If the entity class has named or native query defined, then extract them
     * as part of screening process and store them in {@link NamedQueryStore}.
     * 
     * @param clazz     Entity class
     */
    private void processNamedQueries(Class<?> clazz) {
        NamedNativeQueries nnQueries = clazz.getAnnotation(NamedNativeQueries.class);
        if (nnQueries != null) {
            NamedNativeQuery[] queries = nnQueries.value();
            for (int i = 0; i < queries.length; i ++) {
                namedStore.put(new QueryAttribute(queries[i].name(), queries[i].query(), true));
            }
        }
        NamedNativeQuery nnQuery = clazz.getAnnotation(NamedNativeQuery.class);
        if (nnQuery != null) {
            namedStore.put(new QueryAttribute(nnQuery.name(), nnQuery.query(), true));
        }
    }
    
    /**
     * Process a regular attribute from the entity.
     * @param field 
     */
    private void processAttribute(Class<?> clazz, Field field, EntityAttribute attribute) {
        try {
            // Generate Getter method mapping
            Method getterMethod = clazz.getDeclaredMethod("get"
                    + Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1), new Class[]{});

            getters.put(attribute.column(), getterMethod);

            // Generate Setter method mapping
            Method setterMethod = clazz.getDeclaredMethod("set"
                    + Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1), new Class[] {field.getType()});

            setters.put(attribute.column(), setterMethod);
        }
        catch (NoSuchMethodException | SecurityException e) {
            // do nothing, there could be exceptions at each field level if
            // getters or setters are not defined or access to those methods 
            // is restricted.
        }
    }
    
    @Override
    public Class<?> entityClass() {
        return entityClass;
    }

    @Override
    public String table() {
        return table;
    }

    @Override
    public int attributeCount() {
        return attributes.size();
    }

    @Override
    public Class<?> primaryKey() {
        return primaryKey;
    }

    @Override
    public int idCount() {
        return ids.size();
    }

    @Override
    public EntityAttribute attribute(String name) {
        return attributes.get(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    @Override
    public Iterator<EntityAttribute> ids() {
        return ids.iterator();
    }

    @Override
    public Iterator<EntityAttribute> attributes() {
        return attributes.values().iterator();
    }

    @Override
    public Method setter(String name) {
        return setters.get(name);
    }

    @Override
    public Method getter(String name) {
        return getters.get(name);
    }

    @Override
    public Method pkGetter(String name) {
        return pkGetters.get(name);
    }

    @Override
    public List<RelAttribute> oneToOne() {
        return relationships.get(RelType.OneToOne);
    }

    @Override
    public List<RelAttribute> oneToMany() {
        return relationships.get(RelType.OneToMany);
    }

    @Override
    public List<RelAttribute> manyToOne() {
        return relationships.get(RelType.ManyToOne);
    }

    @Override
    public RelAttribute relation(RelType relType, String name) {
        List<RelAttribute> relAttrs = relationships.get(relType);
        if (relAttrs != null) {
            for (RelAttribute attr : relAttrs) {
                if (attr.name().equals(name)) {
                    return attr;
                }
            }
        }
        return null;
    }

    @Override
    public Index[] indexes() {
        return indexes;
    }
}
