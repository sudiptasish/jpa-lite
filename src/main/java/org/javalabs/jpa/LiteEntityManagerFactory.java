package org.javalabs.jpa;

import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.query.SBPool;
import org.javalabs.jpa.query.SBPoolImpl;
import org.javalabs.jpa.txn.PUInjector;
import org.javalabs.jpa.util.BasicDataSource;
import org.javalabs.jpa.util.ClassScanner;
import org.javalabs.jpa.util.DAOClassScanner;
import org.javalabs.jpa.util.ObjectCreationUtil;
import org.javalabs.jpa.util.PoolDataSource;
import org.javalabs.jpa.util.PostConstructListener;
import jakarta.persistence.Cache;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityListenerRegistration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.SchemaManager;
import jakarta.persistence.Statement;
import jakarta.persistence.StatementReference;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.sql.ResultSetMapping;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA LiTE entity manager factory.
 * 
 * <p>
 * A conventional <code>persistence.xml</code> may have multiple persistence unit
 * defined. An {@link EntityManagerFactory} represents a single persistence unit.
 * If there are multiple units defined, then multiple {@link EntityManagerFactory}s
 * will be created. One persistence unit represents a unique database schema.
 * 
 * <p>
 * A call to {@link EntityManagerFactory#createEntityManager() } will create new
 * {@link EntityManager} instance. Thus there can be multiple {@link EntityManager}s
 * created for a given persistence unit. The factory may maintain a pool of
 * {@link EntityManager}s for better reusability.
 *
 * @author Sudiptasish Chanda
 */

public class LiteEntityManagerFactory implements EntityManagerFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LiteEntityManagerFactory.class);
    
    private final PersistenceHolder holder = PersistenceHolder.getInstance();
    
    private volatile boolean open = false;
    
    // The persistence unit name this entity manager faactory is associated with.
    private final String puName;
    private final Map<String, Object> map;
    
    private SBPool pool;
    private PoolDataSource pooledDs;
    
    private final ClassScanner scanner = new DAOClassScanner();
    
    LiteEntityManagerFactory(String puName, Map<String, Object> map) {
        this.puName = puName;
        this.map = map;
        this.open = true;
    }
    
    /**
     * Return the persistence unit name for this entity manager factory.
     * @return String
     */
    public String getPuName() {
        return puName;
    }
    
    void init() {
        // Initialize the data source.
        PersistenceUnitInfo unitInfo = holder.getCache().get(this.puName);
        
        if (unitInfo.getTransactionType() == PersistenceUnitTransactionType.RESOURCE_LOCAL) {
            // Datasource initialization and dao class scanning will be disabled,
            // if this is a schema generation.
            if (! map.containsKey("schema.gen") || ! map.containsKey("model.gen")) {
                initDataSource(unitInfo);
                initPool(unitInfo);
            }
            // For schema generation, we only need to load the entities.
            processManagedClassed(unitInfo);
            
            if (! map.containsKey("schema.gen") || ! map.containsKey("model.gen")) {
                scan(unitInfo);
                invokeListener(unitInfo);
            }
        }
    }

    /**
     * Initialize the internal query builder pool.
     * @param unitInfo 
     */
    private void initPool(PersistenceUnitInfo unitInfo) {
        Properties props = unitInfo.getProperties();
        String poolSize = props.getProperty("jpa-lite.query.pool.size");
        if (poolSize != null) {
            pool = SBPoolImpl.getInstance(Integer.parseInt(poolSize));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Initialized query buffer pool. Size: {}", poolSize);
            }
        }
        else {
            pool = SBPoolImpl.getInstance();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Initialized default query buffer pool.");
            }
        }
    }

    /**
     * Initialize the custom data source (only if present).
     * Otherwise it will initialize the platform provided {@link BasicDataSource}.
     * 
     * @param unitInfo 
     */
    private void initDataSource(PersistenceUnitInfo unitInfo) {
        String datasource = unitInfo
            .getProperties()
            .getProperty("jpa-lite.data.source");
        
        if (datasource == null || (datasource = datasource.trim()).length() == 0) {
            datasource = "org.javalabs.jpa.util.BasicDataSource";
        }
        // Configure the init parameters.
        Map<String, Object> config = new HashMap<>();
        config.put("datasource.class", map.get("javax.persistence.datasource.class"));
        config.put("db.url", map.get("javax.persistence.jdbc.url"));
        config.put("db.host", map.get("javax.persistence.jdbc.host"));
        config.put("db.port", map.get("javax.persistence.jdbc.port"));
        config.put("db.schema", map.get("javax.persistence.jdbc.schema"));
        config.put("db.user", map.get("javax.persistence.jdbc.user"));
        config.put("db.password", map.get("javax.persistence.jdbc.password"));
        
        
        // All pool specific attributes must start with "db.pool."
        // Handle connection pool related tuning parameters in respective pool provider class.
        for (Map.Entry<String, Object> me : map.entrySet()) {
            if (me.getKey().startsWith("db.pool.")) {
                config.put(me.getKey(), me.getValue());
            }
        }

        // Pass the connection pool related attributes.
        pooledDs = ObjectCreationUtil.create(datasource
            , new Class[] {Map.class}
            , new Object[] {config});
        
        pooledDs.init();
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Initialized data source: {}", datasource);
        }
    }
    
    private void processManagedClassed(PersistenceUnitInfo unitInfo) {
        PersistenceHandler handler = PersistenceHandler.get();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = getClass().getClassLoader();
        }
        try {
            List<String> managedClasses = unitInfo.getManagedClassNames();
            for (String managedClass : managedClasses) {
                Class clazz = loader.loadClass(managedClass);
                handler.createDescriptor(clazz);
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void scan(PersistenceUnitInfo unitInfo) {
        try {
            Properties props = unitInfo.getProperties();
            String scanDir = props.getProperty("jpa-lite.dao.package");
            if (scanDir == null) {
                LOGGER.warn("No scan directory found for DAO class."
                        + " Ignore this message, if this is intentional,"
                        + " otherwise set the attribute jpa-lite.dao.package in persistence.xml file");
                return;
            }
            List<Class> classes = scanner.scan(scanDir.split(","));
            new PUInjector().inject(classes);
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoke the 3rd party listener (if present).
     * @param unitInfo 
     */
    private void invokeListener(PersistenceUnitInfo unitInfo) {
        String listener = unitInfo
            .getProperties()
            .getProperty("jpa-lite.post.construct.listener");
        
        if (listener != null && (listener = listener.trim()).length() > 0) {
            PostConstructListener pcListener = ObjectCreationUtil.create(listener);
            
            // Inject the persistent unit name.
            Properties props = new Properties(unitInfo.getProperties());
            props.setProperty("javax.persistent.unit.name", unitInfo.getPersistenceUnitName());
            pcListener.callback(props);
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Invoked post construct listener: {}", listener);
            }
        }
    }
    
    Connection getPooledConnection() throws SQLException {
        return pooledDs.getCachedConnection();
    }
    
    void closePooledConnection(Connection connection) throws SQLException {
        pooledDs.closeCachedConnection(connection);
    }

    @Override
    public EntityManager createEntityManager() {
        return new LiteEntityManagerImpl(this, getProperties());
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        return new LiteEntityManagerImpl(this, map);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Metamodel getMetamodel() {
        return PersistenceHolder.getInstance().getModel();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() {
        open = false;
    }

    @Override
    public Map<String, Object> getProperties() {
        return map;
    }

    @Override
    public Cache getCache() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // @Override
    public void addNamedQuery(String name, Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityAgent createEntityAgent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityAgent createEntityAgent(Map<?, ?> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SchemaManager getSchemaManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addNamedQuery(String string, jakarta.persistence.Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <R> TypedQueryReference<R> addNamedQuery(String string, TypedQuery<R> tq) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StatementReference addNamedStatement(String string, Statement stmnt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <R> Map<String, TypedQueryReference<R>> getNamedQueries(Class<R> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, StatementReference> getNamedStatements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <E> Map<String, EntityGraph<? extends E>> getNamedEntityGraphs(Class<E> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <R> Map<String, ResultSetMapping<R>> getResultSetMappings(Class<R> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <E> EntityListenerRegistration addListener(Class<E> type, Class<? extends Annotation> type1, Consumer<? super E> cnsmr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void runInTransaction(Consumer<EntityManager> cnsmr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <R> R callInTransaction(Function<EntityManager, R> fnctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <H extends EntityHandler> void runInTransaction(Class<H> type, Consumer<H> cnsmr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <R, H extends EntityHandler> R callInTransaction(Class<H> type, Function<H, R> fnctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}