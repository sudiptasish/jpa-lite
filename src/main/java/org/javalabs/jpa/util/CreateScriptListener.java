package org.javalabs.jpa.util;

import org.javalabs.jpa.FactoryStore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class CreateScriptListener implements PostConstructListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateScriptListener.class);
    
    private static final String DEFAULT_DB_URL = "jdbc:h2:mem:ecmdb;DB_CLOSE_DELAY=-1";

    @Override
    public void callback(Properties props) {
        String create = props.getProperty("javax.persistence.schema-generation.create-database-schemas");
        if ("true".equals(create) && DEFAULT_DB_URL.equals(props.getProperty("javax.persistence.jdbc.url"))) {
            setup(props);
        }
    }
    
    private void setup(Properties props) {
        String puName = props.getProperty("javax.persistent.unit.name");
        EntityManagerFactory emf = FactoryStore.getInstance().getFactory(puName);
        if (emf == null) {
            LOGGER.error("Persistent unit {} is not setup", puName);
            return;
        }
        InputStream in = null;
        EntityManager em = null;

        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            String tableScript = props.getProperty("javax.persistence.schema-generation.scripts.create-target");
            in = getClass().getClassLoader().getResourceAsStream(tableScript);
            
            if (in != null) {
                int i = 0;
                byte[] b = new byte[1024];
                while ((i = in.read(b)) != -1) {
                    bOut.write(b, 0, i);
                }
                bOut.write("\n".getBytes());
                
                bOut.close();
                in.close();
                
                em = emf.createEntityManager();

                em.getTransaction().begin();
                int count = em.createNativeQuery(bOut.toString()).executeUpdate();
                em.getTransaction().commit();

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.debug("Created database table. Update count: {}", count);
                }
            }
            else {
                LOGGER.warn("Table script {} is not found in the persistence.xml");
            }
        }
        catch (IOException | RuntimeException e) {
            if (em != null) {
                em.getTransaction().rollback();
            }
            LOGGER.error("Failed to create the initial set of tables", e);
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (em != null) {
                    em.close();
                }
            }
            catch (IOException e) {
                // Do Nothing.
            }
        }
    }
}
