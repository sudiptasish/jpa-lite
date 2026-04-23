package org.javalabs.jpa;

import org.javalabs.jpa.descriptor.NamedQueryStore;
import org.javalabs.jpa.descriptor.NamedQueryStoreImpl;
import org.javalabs.jpa.descriptor.QueryAttribute;
import org.javalabs.jpa.query.NativeJdbcQuery;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FindOption;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.Query;
import jakarta.persistence.RefreshOption;
import jakarta.persistence.Statement;
import jakarta.persistence.StatementReference;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaStatement;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.sql.ResultSetMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete class to represent a LiTE entity manager.
 *
 * @author Sudiptasish Chanda
 */
public class LiteEntityManagerImpl implements LiteEntityManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LiteEntityManagerImpl.class);
    
    private final LiteEntityManagerFactory emf;
    
    private final NamedQueryStore store = NamedQueryStoreImpl.getStore();
    
    // Parent entity manager.
    // It will be populated for RequiresNow transactional context.
    private LiteEntityManager parent;
    
    // The reader object associated with this entity manager.
    // Any database read operation is redirected via this reader.
    protected final JdbcReader reader;
    
    // The writer object associated with this entity manager.
    // Any database write/update/delete operation is redirected via this writer.
    protected final JdbcWriter writer;
    
    // The properties of the persistence unit this entity manager and the factory
    // class belong to.
    private final Map<String, Object> properties;
    
    // Indicate if this entity manager is open/active.
    // An entity manager will be active immediately after it's instantiation.
    private boolean open = false;
    
    // Entity manager is meant to do some work. Here is the work object that
    // defines the current task.
    // Work object is lazily initialized. When an EntityManager is first obtained
    // it does not have work asigned against it. Whenever client calls any of it's
    // APIs, the work object is initialized.
    private Work work;
    
    // Transaction object started on this entity manager.
    private EntityTransaction txn;
    
    private FlushModeType flushMode = FlushModeType.AUTO;
    
    public LiteEntityManagerImpl(LiteEntityManagerFactory emf, Map<String, Object> properties) {
        this.emf = emf;
        this.properties = properties;
        
        this.reader = new JdbcReader(this);
        this.writer = new JdbcWriter(this);
        
        initUnitOfWork();
        open = true;
    }
    
    private void initUnitOfWork() {
        work = new UnitOfWork(this);
        work.start();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    
    @Override
    public int getBatchSize() {
        try {
            return Integer.valueOf((String)properties.get("jpa-lite.jdbc.batch.size"));
        }
        catch (RuntimeException e) {
            return DEFAULT_BATCH_SIZE;
        }
    }
    
    @Override
    public int getFetchSize() {
        try {
            return Integer.valueOf((String)properties.get("jpa-lite.jdbc.fetch.size"));
        }
        catch (RuntimeException e) {
            return DEFAULT_FETCH_SIZE;
        }
    }
    
    /**
     * Verify that this entity manager is still in open state.
     */
    public void verifyOpen() {
        if (! isOpen()) {
            throw new IllegalStateException("Entity manager is already closed");
        }
    }
    
    @Override
    public void verifyTxn() {
        if (txn == null || ! txn.isActive() || txn.getRollbackOnly()) {
            throw new IllegalStateException("No transaction is started."
                + " Or transaction is not active/set to rollback-only");
        }
    }
    
    /**
     * INTERNAL. Sets transaction to rollback only.
     */
    protected void setRollbackOnly() {
        if (txn != null) {
            txn.setRollbackOnly();
        }
    }

    @Override
    public void persist(Object entity) {
        Objects.requireNonNull(entity);
        verifyOpen();
        verifyTxn();
        
        try {
            writer.add(entity);
        }
        catch (JdbcException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <T> T merge(T entity) {
        Objects.requireNonNull(entity);
        verifyOpen();
        verifyTxn();
        
        try {
            writer.update(entity);
            return entity;
        }
        catch (JdbcException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public void remove(Object entity) {
        Objects.requireNonNull(entity);
        verifyOpen();
        verifyTxn();
        
        try {
            writer.delete(entity);
        }
        catch (JdbcException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return find(entityClass, primaryKey, new HashMap<>());
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return find(entityClass, primaryKey, LockModeType.NONE, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass
        , Object primaryKey
        , LockModeType lockMode
        , Map<String, Object> properties) {
        
        Objects.requireNonNull(primaryKey);
        verifyOpen();
        
        try {
            // Flush the writer before making a select query.
            if (writer.shouldFlush(entityClass)) {
                writer.writeToDb();
            }
            return reader.get(entityClass, primaryKey, lockMode, properties);
        }
        catch (JdbcException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        verifyOpen();
        
        T entity = find(entityClass, primaryKey);
        if (entity == null) {
            throw new EntityNotFoundException("No entity found for type: " + entityClass);
        }
        return entity;
    }

    @Override
    public void flush() {
        verifyOpen();
        
        try {
            writer.writeToDb();
        }
        catch (JdbcException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        verifyOpen();
        this.flushMode = flushMode;
    }

    @Override
    public FlushModeType getFlushMode() {
        return flushMode;
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refresh(Object entity) {
        refresh(entity, new HashMap<>());
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        refresh(entity, LockModeType.NONE, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        Objects.requireNonNull(entity);
        verifyOpen();
        
        try {
            reader.refresh(entity, lockMode, properties);
        }
        catch (JdbcException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public void clear() {
        verifyOpen();
        // Because no persistence context (or L1 cache) is present, therefore
        // this api is a NO-OP.
    }

    @Override
    public void detach(Object entity) {
        verifyOpen();
        // Because no persistence context (or L1 cache) is present, therefore
        // this api is a NO-OP.
    }

    @Override
    public boolean contains(Object entity) {
        verifyOpen();
        // Because no persistence context (or L1 cache) is present, therefore
        // this api will always return false
        return false;
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        verifyOpen();
        properties.put(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        verifyOpen();
        return properties;
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Query createNamedQuery(String name) {
        return createNamedQuery(name, (Class)null);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        QueryAttribute queryAttr = store.get(name);
        if (queryAttr == null) {
            throw new IllegalArgumentException("Named query " + name + " not found");
        }
        return new NativeJdbcQuery(this, queryAttr.query(), resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return createNativeQuery(sqlString, (Class)null);
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String sqlString, Class<T> resultClass) {
        return new NativeJdbcQuery(this, sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void joinTransaction() {
        // NO-OP
    }

    @Override
    public boolean isJoinedToTransaction() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        verifyOpen();
        if (clazz == java.sql.Connection.class) {
            // Get it from the Work unit.
            return (T)work.connection();
        }
        throw new IllegalArgumentException("Unwrapping of " + clazz + " is not supported");
    }

    @Override
    public Object getDelegate() {
        verifyOpen();
        return null;
    }

    @Override
    public void close() {
        verifyOpen();
        
        flush();
        work.complete();
        reader.close();
        writer.close();
        
        open = false;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public EntityTransaction getTransaction() {
        verifyOpen();
        if (txn == null) {
            txn = new EntityTransactionImpl(work);
        }
        return txn;
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Metamodel getMetamodel() {
        verifyOpen();
        return getEntityManagerFactory().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LiteEntityManager parentEm() {
        return this.parent;
    }

    @Override
    public void setParent(LiteEntityManager parent) {
        this.parent = parent;
    }

    @Override
    public <T> T getReference(T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void lock(Object o, LockModeType lmt, LockOption... los) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refresh(Object o, RefreshOption... ros) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createQuery(CriteriaStatement<?> cs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T get(Class<T> type, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T get(Class<T> type, Object o, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T get(EntityGraph<T> eg, Object o, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> List<T> getMultiple(Class<T> type, List<?> list, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> List<T> getMultiple(EntityGraph<T> eg, List<?> list, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T find(Class<T> type, Object o, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T find(EntityGraph<T> eg, Object o, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> List<T> findMultiple(Class<T> type, List<?> list, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> List<T> findMultiple(EntityGraph<T> eg, List<?> list, FindOption... fos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCacheRetrieveMode(CacheRetrieveMode crm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCacheStoreMode(CacheStoreMode csm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createStatement(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Query createQuery(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaSelect<T> cs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createStatement(CriteriaStatement<?> cs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> TypedQuery<T> createQuery(String string, EntityGraph<T> eg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createNamedStatement(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createStatement(StatementReference sr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> TypedQuery<T> createQuery(TypedQueryReference<T> tqr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createNativeStatement(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String string, ResultSetMapping<T> rsm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> EntityGraph<T> getEntityGraph(Class<T> type, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <C> void runWithConnection(ConnectionConsumer<C> cc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <C, T> T callWithConnection(ConnectionFunction<C, T> cf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
