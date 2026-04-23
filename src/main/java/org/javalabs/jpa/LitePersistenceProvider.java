package org.javalabs.jpa;

import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.dialect.Dialect;
import org.javalabs.jpa.dialect.SQLDialect;
import org.javalabs.jpa.meta.MetadataGenerator;
import org.javalabs.jpa.schema.LtSchemaInitializer;
import org.javalabs.jpa.util.ObjectCreationUtil;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceProvider;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.ProviderUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence provider for LiTE jpa framework.
 *
 * @author Sudiptasish Chanda
 */
public class LitePersistenceProvider implements PersistenceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(LitePersistenceProvider.class);
    
    private final PersistenceHolder holder = PersistenceHolder.getInstance();
    
    private final ModelProcessor processor;
    private final MetadataGenerator generator;
    private final PersistenceVerifier verifier;
    
    public LitePersistenceProvider() {
        this.processor = new ModelProcessor(this);
        this.generator = new MetadataGenerator(this);
        this.verifier = new PersistenceVerifier(this);
    }

    @Override
    public EntityManagerFactory createEntityManagerFactory(String puName, Map map) {
        EntityManagerFactory factory = FactoryStore.getInstance().getFactory(puName);
        
        if (factory == null) {
            try {
                // Load the persistence config file.
                processor.process(puName);
                
                override(puName, map);
                
                // If this is not a schema generation, then verify the persistence properties.
                // For schema generation we only need the dialect, which will either 
                // be passed as a command line argument tp SchemaGenerator or picked up
                // from persistence.xml
                if (map == null || ! map.containsKey("schema.gen")) {
                    verifier.verify(puName);
                }

                // Process the managed classes to generate the metamodel.
                generator.generate(puName);

                // Class descriptor will be generated lazily at the time of
                // making database calls.

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Initialized LiTE Persistence Provider");
                }
            }
            catch (Exception e) {
                LOGGER.error("Error initializing LiTE Persistence Provider", e);
                throw new RuntimeException(e);
            }
            // Now, validate ...
            PersistenceUnitInfo unitInfo = holder.getCache().get(puName);
            if (unitInfo == null) {
                throw new RuntimeException("Invalid persistent unit name [" + puName + "] specified");
            }

            Properties props = unitInfo.getProperties();
            
            // Now create the sql dialect object and inject it in the map.
            Dialect dialect = Enum.valueOf(Dialect.class, props.getProperty("jpa-lite.dialect").toUpperCase());
            SQLDialect sqlDialect = ObjectCreationUtil.create(dialect.dialectClass());
            
            if (map == null) {
                map = new HashMap<String, Object>();
            }
            map.putAll(props);
            map.put("dialect", sqlDialect);
            
            factory = new LiteEntityManagerFactory(puName, map);
            
            // Add it to the factory store.
            FactoryStore.getInstance().addFactory(puName, factory);
            ((LiteEntityManagerFactory)factory).init();
        }
        return factory;
    }

    private void override(String puName, Map map) {
        PersistenceUnitInfo unitInfo = holder.getCache().get(puName);
        Properties props = unitInfo.getProperties();
        
        // Override ...
        if (map != null) {
            for (Object obj : map.entrySet()) {
                Map.Entry<Object, Object> me = (Map.Entry<Object, Object>)obj;
                props.setProperty((String)me.getKey(), (String)me.getValue());
            }
        }

        // Derive sql dialect if not already provided.
        if (! props.containsKey("jpa-lite.dialect")) {
            String jdbcUrl = props.getProperty("javax.persistence.jdbc.url");
            for (Dialect dialect : Dialect.values()) {
                if (jdbcUrl.contains(dialect.name().toLowerCase())) {
                    props.setProperty("jpa-lite.dialect", dialect.name().toLowerCase());
                }
            }
            if (! props.containsKey("jpa-lite.dialect")) {
                // If stillnot available, throw an error.
                throw new RuntimeException("Invalid jdbc url " + jdbcUrl + " specified."
                        + " Supported databases: " + Arrays.toString(dialects()));
            }
        }
    }

    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void generateSchema(PersistenceUnitInfo info, Map map) {
        LtSchemaInitializer initializer = new LtSchemaInitializer();
        initializer.generateDDL(info, map);
    }

    @Override
    public boolean generateSchema(String persistenceUnitName, Map map) {
        createEntityManagerFactory(persistenceUnitName, map);
        
        PersistenceUnitInfo unitInfo = holder.getCache().get(persistenceUnitName);
        generateSchema(unitInfo, map);
        return true;
    }

    @Override
    public ProviderUtil getProviderUtil() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Return the set of dialects currently supported by ecm.
     * @return String[]
     */
    private String[] dialects() {
        Dialect[] dialects = Dialect.values();
        String[] dVals = new String[dialects.length];
        
        for (int i = 0; i < dialects.length; i ++) {
            if (Dialect.POSTGRES == dialects[i]) {
                dVals[i] = dialects[i].name().toLowerCase() + " (default)";
            }
            else {
                dVals[i] = dialects[i].name().toLowerCase();
            }
        }
        return dVals;
    }

    @Override
    public EntityManagerFactory createEntityManagerFactory(PersistenceConfiguration pc) {
        EntityManagerFactory factory = FactoryStore.getInstance().getFactory(pc.name());
        
        if (factory == null) {
            processor.process(pc);
            
            factory = new LiteEntityManagerFactory(pc.name(), pc.properties());
            
            // Add it to the factory store.
            FactoryStore.getInstance().addFactory(pc.name(), factory);
            ((LiteEntityManagerFactory)factory).init();
        }
        return factory;
    }

    @Override
    public boolean generateSchema(PersistenceConfiguration pc) {
        PersistenceHandler handler = PersistenceHandler.get();
        for (Class clazz : pc.managedClasses()) {
            handler.createDescriptor(clazz);
        }
        LtSchemaInitializer initializer = new LtSchemaInitializer();
        initializer.generateDDL(pc);
        
        return Boolean.TRUE;
    }

    @Override
    public ClassTransformer getClassTransformer(PersistenceUnitInfo pui, Map<?, ?> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
