package org.javalabs.jpa.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class LtModelInitializer {
    
    private static final String NEW_LINE      = "\n";
    private static final String TAB           = "    ";
    private static final String SPACE         = " ";
    private static final String SEMICOLON     = ";";
    private static final String NATIVE_QUERY_FORMAT = "(name = \"{0}\", query = \"{1}\")";
    
    private final CommentGenHelper commentGen = new CommentGenHelper();
    private final FieldGenHelper fieldGen = new FieldGenHelper();
    private final ConstructorGenHelper constructorGen = new ConstructorGenHelper();
    private final MethodGenHelper methodGen = new MethodGenHelper();
    private final HashCodeGenHelper hashGen = new HashCodeGenHelper();
    private final EqualsGenHelper equalsGen = new EqualsGenHelper();
    
    public Map<String, String> generateModels(String ormXml) {
        try {
            Class<?> clazz = Class.forName("org.javalabs.decl.gen.JaxbJpaConverterBridge");
            Constructor cons = clazz.getDeclaredConstructor(new Class[] {});
            Object obj = cons.newInstance(new String[] {});
            
            Method method = clazz.getDeclaredMethod("toRawClass", new Class[] {String.class});
            return (Map<String, String>)method.invoke(obj, new String[] {ormXml});
            
        }
        catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
//    public List<String> generateModels(String pkgName, List<EntityType> entities) {
//        List<String> models = new ArrayList<>(entities.size());
//        
//        StringBuilder buff = new StringBuilder(20140);
//        
//        for (EntityType entity : entities) {
//            String className = entity.getClazz();
//            
//            if (pkgName != null && pkgName.trim().length() > 0) {
//                buff.append("package").append(SPACE).append(pkgName).append(SEMICOLON);
//                buff.append(NEW_LINE).append(NEW_LINE);
//            }
//            // Add other imports based on field data type.
//            AttributesType attrs = entity.getAttributes();
//            
//            for (IdType id : attrs.getId()) {
//                if (! id.getType().startsWith("java.lang.")
//                        && ! field.getType().isArray()
//                        && ! entity.getImports().contains(field.getType().getName())) {
//                    
//                    entity.getImports().add(field.getType().getName());
//                }
//            }
//            // Check if this entity has a primary key, otherwise remove the pk related imports.
//            if (entity.getPkfields() == null || entity.getPkfields().isEmpty()) {
//                entity.getImports().remove("jakarta.persistence.Id");
//                entity.getImports().remove("jakarta.persistence.IdClass");
//                entity.getImports().remove("java.util.Objects");
//            }
//            for (String imp : entity.getImports()) {
//                buff.append("import").append(SPACE).append(imp).append(SEMICOLON);
//                buff.append(NEW_LINE);
//            }
//            
//            buff.append(NEW_LINE);
//            if (entity.getComment() != null) {
//                commentGen.generateComment(buff, entity, 1);
//            }
//            
//            // Jpa Annotation - Start
//            buff.append("@Entity");
//            buff.append(NEW_LINE);
//            
//            if (entity.getNativeQueries() != null && ! entity.getNativeQueries().isEmpty()) {
//                buff.append("@NamedNativeQueries({");
//                for (Map.Entry<String, String> me : entity.getNativeQueries().entrySet()) {
//                    buff.append(NEW_LINE).append(TAB).append("@NamedNativeQuery")
//                            .append(MessageFormat.format(NATIVE_QUERY_FORMAT, me.getKey(), me.getValue()));
//                }
//                buff.append(NEW_LINE);
//                buff.append("})");
//                buff.append(NEW_LINE);
//            }
//            buff.append("@Table").append("(")
//                    .append("name").append(SPACE).append("=").append(SPACE).append("\"").append(entity.getTable()).append("\"")
//                    .append(")");
//            buff.append(NEW_LINE);
//            
//            if (entity.getPkfields() != null && ! entity.getPkfields().isEmpty()) {
//                buff.append("@IdClass").append("(").append(className).append(".").append(className).append("PK").append(".class").append(")");
//                buff.append(NEW_LINE);
//            }
//            // Jpa Annotation - End
//            
//            // Add class name
//            buff.append("public").append(SPACE).append("class").append(SPACE)
//                    .append(className).append(SPACE).append("implements").append(SPACE).append("Serializable").append(SPACE).append("{");
//            buff.append(NEW_LINE).append(NEW_LINE);
//            
//            // Add all fields
//            for (JpaField field : entity.getFields()) {
//                fieldGen.generateFieldDef(buff, field, true, 1);
//            }
//            // Add constructor
//            constructorGen.generateConstructorDef(buff, className, null, 1, false);
//            
//            // Add methods (getter and setter)
//            for (JpaField field : entity.getFields()) {
//                methodGen.generateMethodDef(buff, field, 1);
//            }
//            // Add the PK class.
//            if (entity.getPkfields() != null && ! entity.getPkfields().isEmpty()) {
//                List<JpaField> pkFields = entity.getPkfields();
//                
//                String pkClassName = className + "PK";
//                buff.append(TAB).append("public").append(SPACE).append("static").append(SPACE).append("class").append(SPACE)
//                        .append(pkClassName).append(SPACE).append("implements").append(SPACE).append("Serializable").append(SPACE).append("{");
//                buff.append(NEW_LINE).append(NEW_LINE);
//                
//                // Add field(s)
//                for (JpaField pkField : pkFields) {
//                    fieldGen.generateFieldDef(buff, pkField, false, 2);
//                }
//                
//                // Add constructor(s)
//                constructorGen.generateConstructorDef(buff, pkClassName, pkFields, 2, true);
//                
//                // Add getter and setter
//                for (JpaField pkField : pkFields) {
//                    methodGen.generateMethodDef(buff, pkField, 2);
//                }
//                // Add hash() code
//                hashGen.generateHashCodeDef(buff, pkFields, 2);
//                
//                // Add equals() method
//                equalsGen.generateEqualsDef(buff, pkClassName, pkFields, 2);
//                
//                // End inner PK class
//                buff.append(NEW_LINE);
//                buff.append(TAB).append("}");
//            }
//            // End outer Entity class
//            buff.append(NEW_LINE);
//            buff.append("}");
//            
//            models.add(buff.toString());
//            
//            buff.delete(0, buff.length());
//        }
//        return models;
//    }
}
