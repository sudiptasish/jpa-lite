package org.javalabs.jpa.entity;

import org.javalabs.jpa.dialect.Dialect;
import org.javalabs.jpa.dialect.SQLDialect;
import org.javalabs.jpa.util.ObjectCreationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class MetadataExtractor {
    
    private static final String AUTHOR = "schan280";
    
    private static final String COMMENT = "This class is generated using jpa-lite framework";
    
    private static final String[] IMPORTS = {
            "jakarta.persistence.Column"
            , "jakarta.persistence.Entity"
            , "jakarta.persistence.Id"
            , "jakarta.persistence.IdClass"
            , "jakarta.persistence.NamedNativeQueries"
            , "jakarta.persistence.NamedNativeQuery"
            , "jakarta.persistence.Table"
            , "java.io.Serializable"
            , "java.util.Objects"
    };
    
    MetadataExtractor() {}
    
    public String extract(String projectDir, Map<String, Object> params) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        
        try {
            // Create the appropriate dialect.
            String dialectName = (String)params.get("jpa-lite.dialect");
            Dialect dialect = Enum.valueOf(Dialect.class, dialectName.toUpperCase());
            
            SQLDialect sqlDialect = ObjectCreationUtil.create(dialect.dialectClass());
            
            // Create the url.
            String host = (String)params.get("javax.persistence.jdbc.host");
            String port = (String)params.get("javax.persistence.jdbc.port");
            String db = (String)params.get("javax.persistence.jdbc.db");
            params.put("javax.persistence.jdbc.url", sqlDialect.db_url(host, port, db));
            
            PersistenceConfiguration config = new PersistenceConfiguration("model.gen");
            config.properties(params);
            for (Class<?> clazz : sqlDialect.metadataClasses()) {
                config.managedClass(clazz);
            }
            Class<?> clazz = Class.forName("jakarta.persistence.Persistence");
            Method method = clazz.getDeclaredMethod("createEntityManagerFactory", new Class[] {PersistenceConfiguration.class});
            emf = (EntityManagerFactory)method.invoke(null, new Object[] {config});
            
            em = emf.createEntityManager();
            
            return sqlDialect.db_table_metadata(em, params);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (em != null) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }
}
