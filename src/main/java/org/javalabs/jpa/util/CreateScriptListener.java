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
 * Listener responsible for executing database creation scripts defined
 * in {@code persistence.xml}.
 *
 * <p>The {@code CreateScriptListener} is invoked during persistence unit
 * initialization to process and execute table creation (DDL) scripts
 * configured via JPA properties. It typically reads script locations
 * specified in {@code persistence.xml} and runs them against the target
 * database.</p>
 *
 * <p>This listener is commonly used to:</p>
 * <ul>
 *   <li>Initialize database schema during application startup</li>
 *   <li>Execute DDL scripts for table creation</li>
 *   <li>Support environments where schema generation is script-driven</li>
 * </ul>
 *
 * <p>The script execution behavior may depend on standard JPA properties
 * such as:</p>
 * <ul>
 *   <li>{@code javax.persistence.schema-generation.create-script-source}</li>
 *   <li>{@code javax.persistence.schema-generation.database.action}</li>
 * </ul>
 *
 * <p>Implementations may handle concerns such as:</p>
 * <ul>
 *   <li>Script parsing and execution order</li>
 *   <li>Error handling and logging</li>
 *   <li>Transaction boundaries during script execution</li>
 * </ul>
 *
 * <p><b>Lifecycle:</b> Typically triggered once during persistence unit
 * bootstrap after {@code persistence.xml} has been processed.</p>
 *
 * <p><b>Thread safety:</b> Not required to be thread-safe as it is generally
 * executed during single-threaded initialization.</p>
 *
 * <p><b>Note:</b> Care should be taken to avoid re-running scripts in
 * environments where schema already exists.</p>
 *
 * @author Sudiptasish Chanda
 */
public class CreateScriptListener implements PostConstructListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateScriptListener.class);
    
    private static final String DEFAULT_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

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
