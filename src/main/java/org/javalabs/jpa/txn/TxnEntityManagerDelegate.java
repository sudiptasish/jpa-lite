package org.javalabs.jpa.txn;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TxnEntityManagerDelegate implements EntityManager {
    
    private static final ThreadLocal<EntityManager> tlProxy = new ThreadLocal<>();
    
    TxnEntityManagerDelegate() {}
    
    void setProxy(EntityManager proxy) {
        this.tlProxy.set(proxy);
    }
    
    void unsetProxy() {
        this.tlProxy.remove();
    }

    @Override
    public void persist(Object entity) {
        tlProxy.get().persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        return tlProxy.get().merge(entity);
    }

    @Override
    public void remove(Object entity) {
        tlProxy.get().remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return tlProxy.get().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return tlProxy.get().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return tlProxy.get().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return tlProxy.get().getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        tlProxy.get().flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        tlProxy.get().setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return tlProxy.get().getFlushMode();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        tlProxy.get().lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        tlProxy.get().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity) {
        tlProxy.get().refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        tlProxy.get().refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        tlProxy.get().refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        tlProxy.get().clear();
    }

    @Override
    public void detach(Object entity) {
        tlProxy.get().detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return tlProxy.get().contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return tlProxy.get().getLockMode(entity);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        tlProxy.get().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return tlProxy.get().getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        return tlProxy.get().createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return tlProxy.get().createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return tlProxy.get().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        return tlProxy.get().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return tlProxy.get().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return tlProxy.get().createNativeQuery(sqlString);
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String sqlString, Class<T> resultClass) {
        return tlProxy.get().createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return tlProxy.get().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return tlProxy.get().createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return tlProxy.get().createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        return tlProxy.get().createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        return tlProxy.get().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        tlProxy.get().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return tlProxy.get().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return tlProxy.get().unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return tlProxy.get().getDelegate();
    }

    @Override
    public void close() {
        tlProxy.get().close();
    }

    @Override
    public boolean isOpen() {
        return tlProxy.get().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return tlProxy.get().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return tlProxy.get().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return tlProxy.get().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return tlProxy.get().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return tlProxy.get().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return tlProxy.get().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return tlProxy.get().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return tlProxy.get().getEntityGraphs(entityClass);
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
