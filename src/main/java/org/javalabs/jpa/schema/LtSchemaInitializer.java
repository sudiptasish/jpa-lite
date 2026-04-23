package org.javalabs.jpa.schema;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.dialect.Dialect;
import org.javalabs.jpa.dialect.SQLDialect;
import org.javalabs.jpa.util.ObjectCreationUtil;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.spi.PersistenceUnitInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA-LiTE schema initializer.
 * 
 * <p>
 * This class is responsible for generating table scripts from the managed entities.
 *
 * @author schan280
 */
public class LtSchemaInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LtSchemaInitializer.class);
    
    public LtSchemaInitializer() {}

    public boolean generateDDL(PersistenceConfiguration config) {
        return generateInternal(config.properties(), config.managedClasses());
    }

    public boolean generateDDL(PersistenceUnitInfo unitInfo, Map<String, Object> map) {
        Map<String, Object> props = unitInfo.getProperties().entrySet().stream()
                .collect(Collectors.toMap(
                    e -> e.getKey().toString(),
                    e -> e.getValue().toString()
                ));
        return generateInternal(props, PersistenceHandler.get().entities());
    }
    
    private boolean generateInternal(Map<String, Object> props, List<Class<?>> classes) {
        try {
            String mode = (String)props.get("javax.persistence.schema-generation.scripts.action");
            String dialectName = (String)props.get("jpa-lite.dialect");

            if (dialectName == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("No dialect is present. DDL script generation will be skipped");
                }
                return false;
            }
            Dialect dialect = Enum.valueOf(Dialect.class, dialectName.toUpperCase());

            String file = (String)props.get("javax.persistence.schema-generation.scripts.create-target");
            if (file == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("No output script file is specified. DDL script generation will be skipped");
                }
                return false;
            }
            // If the file is placed in a classpath directory, ensure you strip off the directory path.
            // int idx = file.lastIndexOf("/");
            // if (idx > 0) {
            //     file = file.substring(idx + 1);
            // }
            SQLDialect sqlDialect = ObjectCreationUtil.create(dialect.dialectClass());

            if ("drop-and-create".equals(mode) || "drop".equals(mode)) {
                generateDropScript(sqlDialect, file);
            }

            if ("create".equals(mode) || "drop-and-create".equals(mode)) {
                generateCreateScript(sqlDialect
                        , file
                        , classes
                        , (String)props.get("javax.persistence.sql-load-script-source"));

                // Now run the script against the db.
                /*String create = unitInfo.getProperties().getProperty("javax.persistence.schema-generation.create-database-schemas");
                if ("true".equals(create)) {
                    EntityManagerFactory emf = FactoryStore.getInstance().getFactory(unitInfo.getPersistenceUnitName());
                    EntityManager em = null;

                    String schema = unitInfo.getProperties().getProperty("javax.persistence.jdbc.user");
                    String script = new String(Files.readAllBytes(Paths.get(file)));

                    try {
                        em = emf.createEntityManager();
                        List<Object> list = em.createNativeQuery(sqlDialect.query_tables(schema)).getResultList();

                        // Create only if it is an empty schema.
                        if (list.isEmpty()) {
                            em.getTransaction().begin();
                            int i = em.createNativeQuery(script).executeUpdate();
                            em.getTransaction().commit();

                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info("Created db table. Update count: {}", i);
                            }
                        }
                        else {
                            LOGGER.warn("Skipped db creation, as they already exist in the DB");
                        }
                    }
                    catch (RuntimeException e) {
                        if (em != null) {
                            em.getTransaction().rollback();
                        }
                        LOGGER.error("Failed to create db table", e);
                        throw e;
                    }
                    finally {
                        if (em != null) {
                            em.close();
                        }
                    }
                }*/
            }
        }
        catch (IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    
    /**
     * Generate the create table script for this database.
     * 
     * @param dialect
     * @param file
     * @throws IOException 
     */
    private void generateCreateScript(SQLDialect sqlDialect
            , String filename
            , List<Class<?>> entities
            , String startScript) throws IOException {
        
        StringBuilder buff = new StringBuilder(4096);
        
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        
        buff.append("-- Run the below script to generate the tables --");
        buff.append("\n").append(sqlDialect.run_command(file.getAbsolutePath()));
        buff.append("\n\n");
        
        // Create the table script.
        buff.append("-- Table Script --");
        buff.append("\n\n");
        
        for (Class<?> entity : entities) {
            ClassDescriptor desc = PersistenceHandler.get().getDescriptor(entity);
            if (desc == null) {
                throw new IllegalArgumentException("Object " + entity.getName() + " is not a registered jpa entity");
            }
            System.out.println("Generating table script for: " + desc.table());
      
            buff.append(sqlDialect.create_table(desc));
            buff.append("\n\n");
        }

        // Create the primary key script.
        buff.append("-- Primary Key Constraint --");
        buff.append("\n\n");
        String sql = "";

        for (Class<?> entity : PersistenceHandler.get().entities()) {
            sql = sqlDialect.primary_key(PersistenceHandler.get().getDescriptor(entity));
            if (sql.length() > 0) {
                buff.append(sql);
                buff.append("\n\n");
            }
        }

        // Create the foreign key script.
        buff.append("-- Foreign Key Constraint --");
        buff.append("\n\n");
        for (Class<?> entity : PersistenceHandler.get().entities()) {
            sql = sqlDialect.foreign_key(PersistenceHandler.get().getDescriptor(entity));
            if (sql.length() > 0) {
                buff.append(sql);
                buff.append("\n\n");
            }
        }

        // Create the index script.
        buff.append("-- Indexes --");
        buff.append("\n\n");
        for (Class<?> entity : PersistenceHandler.get().entities()) {
            sql = sqlDialect.index(PersistenceHandler.get().getDescriptor(entity));
            if (sql.length() > 0) {
                buff.append(sql);
                buff.append("\n\n");
            }
        }
        
        // Now, check if any custom initial script is provided.
        if (startScript != null && startScript.trim().length() > 0) {
            InputStream in = stream(startScript);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            if (in != null) {
                buff.append("-- Init Script --");
                buff.append("\n\n");

                int i = 0;
                byte[] b = new byte[1024];
                while ((i = in.read(b)) != -1) {
                    bOut.write(b, 0, i);
                }
                bOut.close();
                in.close();

                buff.append(bOut.toString());
                buff.append("\n");
            }
        }
        
        // Dump it to file.
        Files.write(Paths.get(file.getAbsolutePath())
                , buff.toString().getBytes()
                , StandardOpenOption.CREATE);
    }
    
    /**
     * Generate the drop table script for this database.
     * 
     * @param dialect
     * @param file
     * @throws IOException 
     */
    private void generateDropScript(SQLDialect sqlDialect, String filename) throws IOException {
        StringBuilder buff = new StringBuilder(4096);
        
        String dropFile = "";
        int idx = filename.lastIndexOf(".");
        if (idx > 0) {
            dropFile = filename.substring(0, idx) + "_drop" + filename.substring(idx);
        }
        else {
            dropFile = filename + "_drop";
        }

        File file = new File(dropFile);
        if (file.exists()) {
            file.delete();
        }
        
        buff.append("-- Run the below script to drop all the tables --");
        buff.append("\n").append(sqlDialect.run_command(file.getAbsolutePath()));
        buff.append("\n\n");
        
        for (Class<?> entity : PersistenceHandler.get().entities()) {
            ClassDescriptor desc = PersistenceHandler.get().getDescriptor(entity);
            if (desc == null) {
                throw new IllegalArgumentException("Object " + entity.getName() + " is not a registered jpa entity");
            }
            buff.append(sqlDialect.drop_table(desc));
            buff.append("\n");
        }

        // Dump it to file.
        Files.write(Paths.get(dropFile)
                , buff.toString().getBytes()
                , StandardOpenOption.CREATE);
    }
    
    /**
     * Read the file and return a handle {@link InputStream} to this file.
     * If the file is not found in the specific directory or in the classpath, then
     * this API will return <code>null</code>.
     * 
     * @param filename
     * @return InputStream
     * @throws FileNotFoundException 
     */
    public static InputStream stream(final String filename) throws FileNotFoundException {
        // File input stream for the file to be read
        InputStream in = null;

        File file = new File(filename);
        if (file.exists()) {
            in = new FileInputStream(file);
        }
        else {
            in = LtSchemaInitializer.class.getClassLoader().getResourceAsStream(filename);
            if (in != null) {

            }
            else {
                URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                if (url != null) {
                    String urlFile = url.getFile();
                    urlFile = urlFile.replaceAll("%20", " ");
                    in = new FileInputStream(new File(urlFile));
                }
            }
        }
        return in;
    }
}
