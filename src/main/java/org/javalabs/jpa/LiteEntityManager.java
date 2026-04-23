package org.javalabs.jpa;

import jakarta.persistence.EntityManager;

/**
 * Native Entity Manager to interact with the persistence context.
 * 
 * This is a wrapper over provider specific entity manager to bypass most of the complex 
 * processing logic that is otherwise required for every db operation that a conventional
 * entity manager performs.
 * Although JPA/ORM framework has multiple advantages, like ...
 * 1. It allows us to avoid writing DML in the database specific dialect of SQL.
 * 2. Allows to load and save Java objects and graphs without any DML language at all.
 * 3. Moreover, it is simpler, cleaner and less labour intensive than JDBC + SQL.
 * 
 * However, if performance is an overriding concern, JPA does tend to get in the way
 * by adding layers between the application and the database. If the application requires
 * extensively hand-optimized native database queries and schemas to maximize performance,
 * JPA may not be a good fit. It also depends as to how one uses JPA in his/her code.
 * Sometimes poor modelling/query may lead to lot of performance issue. Because targetmodel
 * code is tightly integrated with JPA, hence it is practically impossible to remove
 * the extra ORM layer and convert all JPQL/Criteria Query/Entity manager into native
 * JDBC SQL. Also writing native SQL is always error prone and eat away most of the
 * developer's time.
 * 
 * Hence it was decided to introduce a light-weight ORM framework which can easily 
 * bypass the overahead of existing JPA provider (TopLink/EclipseLink), but at the
 * same time keep the current code intact.
 * 
 * Native Entity Manager is a light weight Entity Manager which follows the JPA
 * specification, but all it's APIs are light-weight. It goes against the concept of
 * "managed" vs "non-managed" entities. Unlike JPA, it does not maintain any local
 * L1 cache or participates any distributed L2 cache, instead every invocation of 
 * it's APIs would make direct call to DB to persist / fetch any data.
 * 
 * What is supported:
 * 1. EntityManager.find
 * 2. EntityManager.persist
 * 3. EntityManager.merge
 * 4. EntityManager.remove
 * 5. EntityManager.getTransaction
 * 6. All variants of EntityManager.createNamedQuery. It supports only NamedNative query.
 * 
 * What is not supported:
 * 1. EntityManager.refresh
 * 2. Any Criteria based query.
 * 3. JPQL.
 * 
 * What is new:
 * 1. Support for Stored Procedure (both parameterized and non-parameterized).
 * 3. Support for Vendor specific Hint.
 *
 * @author Sudiptasish Chanda
 */
public interface LiteEntityManager extends EntityManager {
    
    int DEFAULT_FETCH_SIZE = 1000;
    int DEFAULT_BATCH_SIZE = 4000;
    
    /**
     * Return the batch size for this entity manager.
     * If the custom batch size is specified in the configuration file, use it,
     * otherwise return the eclipselink batch writing size.
     * 
     * @return int
     */
    int getBatchSize();
    
    /**
     * Return the fetch size for this entity manager.
     * If the custom batch size is specified in the configuration file, use it,
     * otherwise return the eclipselink fetch size.
     * 
     * @return int
     */
    int getFetchSize();
    
    /**
     * Check that a transaction is started.
     * 
     * For any kind of write operation a transaction must be started. E.g., the APIs
     * like {@link #persist(java.lang.Object) }, {@link #merge(java.lang.Object) 
     * and {@link #remove(java.lang.Object) } check the presence of a transaction.
     * If no transaction has started, then an exception will be thrown.
     */
    void verifyTxn();
    
    /**
     * Set the currently active entity manager as a parent of newly created entity manager.
     * 
     * Sometimes it is possible for applicaation component to start a new pragma
     * transaction even if one transaction is in progress. In which case the current
     * active entity manager has to be backed up, before spawning a new entity manager.
     * 
     * @param parent 
     */
    void setParent(LiteEntityManager parent);
    
    /**
     * Return the parent entity manager.
     * 
     * Sometimes it is possible for applicaation component to start a new pragma
     * transaction even if one transaction is in progress. In which case the current
     * active entity manager has to be backed up, before spawning a new entity manager.
     * The new entity manager will thus become a child of it.
     * 
     * @return LiteEntityManager
     */
    LiteEntityManager parentEm();
}
