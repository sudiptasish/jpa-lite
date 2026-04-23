package org.javalabs.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class JPADBExtension implements BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JPADBExtension.class);
    
    private static final String TEST_RESOURCE_FILE = "jpa-test.properties";
    private static final String PERSISTENCE_UNIT = "jpa-pu";
    
    private EntityManagerFactory emf = null;
    
    @Override
    public void beforeAll(ExtensionContext ec) throws Exception {
        String uniqueKey = getClass().getName();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Invoked Database Extension::beforeAll. Test Class: {}", uniqueKey);
        }
        Object value = ec.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).get(uniqueKey + "~DB");
        
        if (value == null) {
            // First test container invocation.
            System.setProperty("orm.config.file", "persistence-test.xml");
            
            setup();
            
            ec.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(uniqueKey + "~DB", emf);
        }
        else {
            LOGGER.warn("DBExtension is already initialized");
        }
    }
    
    protected void setup() throws Exception {
        // Read the test configuration
        Properties prop = configureEnv();

        // Configure the application.
        configureApp(prop);
        
        // Perform cleanup
        // cleanupDB(prop);

        // Create the db table
        createDefaultTable(prop);
    }
    
    /**
     * Set the environment variables after reading them from the properties file.
     * @return Properties
     */
    protected Properties configureEnv() {
        String suffix = System.getProperty("deployment", "epaas");
        
        if (System.getProperty("os.name").contains("Mac")) {
            suffix = "local";
        }
        
        InputStream iStream = null;
        boolean found = true;
        try {
            Properties prop = new Properties();
            URL url = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(TEST_RESOURCE_FILE + "." + suffix);
            
            if (url == null) {
                // Take the common file.
                url = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(TEST_RESOURCE_FILE);
                
                found = false;
            }

            String filename = url.getFile();
            iStream = new FileInputStream(filename);
            prop.load(iStream);
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Read test configuration from file: {}. Props: {}"
                    , TEST_RESOURCE_FILE + (found ? ("." + suffix) : "")
                    , prop);
            }
            return prop;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (iStream != null) {
                    iStream.close();
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void configureApp(Properties prop) throws Exception {
        // Set the database config properties.
        Map map = new HashMap();
        if (prop.getProperty("db.url") != null) {
            map.put("javax.persistence.jdbc.url", prop.getProperty("db.url"));
        }
        if (prop.getProperty("db.user") != null) {
            map.put("javax.persistence.jdbc.user", prop.getProperty("db.user"));
        }
        if (prop.getProperty("db.password") != null) {
            map.put("javax.persistence.jdbc.password", prop.getProperty("db.password"));
        }
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, map);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Configured application. Entity manager factory is initialized");
        }
    }
    
    private void cleanupDB(Properties prop) throws Exception {
        if (prop.getProperty("db.cleanup", "true").equals("true")) {
            // Read the db script to be executed
            String script = read("db_cleanup.sql");

            // Execute db script
            int count = execute("Cleanup", script);
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Performed database cleanup. Response: {}", count);
            }
        }
    }

    private void createDefaultTable(Properties prop) throws Exception {
        if (prop.getProperty("db.recreate", "true").equals("true")) {
            // Read the db script to be executed
            String script = read("table.sql");

            // Execute db script
            int count = execute("Create", script);
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Created Application Metadata table. Response: {}", count);
            }
        }
    }
    
    private String read(String sqlScript) throws Exception {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(1024);
        InputStream in = null;
        
        try {
            // Run the table script.
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(sqlScript);
            byte[] buff = new byte[2048];
            int read = 0;
            
            while ((read = in.read(buff)) != -1) {
                bOut.write(buff, 0, read);
            }
            bOut.close();
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Read table script from file: {}. Size: {}", sqlScript, bOut.size());
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                throw e;
            }
        }
        return bOut.toString();
    }  
    
    private int execute(String op, String script) {
        EntityManager em = null;
        
        try {
            em = emf.createEntityManager();
            
            em.getTransaction().begin();
            int i = em.createNativeQuery(script).executeUpdate();
            em.getTransaction().commit();
            
            return i;
        }
        catch (Exception e) {
            LOGGER.error("Error executing script. Operation: " + op, e);
            em.getTransaction().rollback();
            throw e;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext ec) throws Exception {
        String uniqueKey = getClass().getName();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Invoked Database Extension::afterAll. Test Class: {}", uniqueKey);
        }
        
        emf = (EntityManagerFactory)ec.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .get(uniqueKey + "~DB");
            
        Properties prop = configureEnv();
        cleanupDB(prop);
        
        ec.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).remove(uniqueKey + "~DB");
    }

    @Override
    public void close() throws Throwable {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Invoked Database Extension::close");
        }
        if (emf != null) {
            emf.close();
        }
    }
}
