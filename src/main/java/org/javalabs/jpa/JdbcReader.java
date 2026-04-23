package org.javalabs.jpa;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.descriptor.QueryCache;
import org.javalabs.jpa.descriptor.RelAttribute;
import org.javalabs.jpa.descriptor.RelAttribute.Join;
import org.javalabs.jpa.query.ParameterImpl;
import org.javalabs.jpa.query.SelectQuery;
import org.javalabs.jpa.util.QueryHints;
import jakarta.persistence.FetchType;
import jakarta.persistence.LockModeType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC Reader object.
 * 
 * This object is used to fetch data from the database.
 * Every native entity manager will have it's dedicated reader object. Reader is
 * not thread safe, however, since entity manager can not be shared across multiple
 * threads, thus making this class implicitly thread safe.
 * 
 * Reader object can support two kind of queries:
 * 1. Simple SQL query
 * 2. Complex query (using SQL type).
 *
 * @author Sudiptasish Chanda
 */
public class JdbcReader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcReader.class);
    
    // Underlying entity manager.
    private final LiteEntityManager em;
    
    // A reader can support only one kind of query:
    // 1. Select by primary key
    
    private final SelectQuery query;
    
    JdbcReader(LiteEntityManager em) {
        this.em = em;
        this.query = new SelectQuery(em);
    }

    /**
     * Return the underlying entity manager, this writer is associated with.
     * @return LiteEntityManager
     */
    public LiteEntityManager getEm() {
        return em;
    }
    
    /**
     * Fetch the object from the underlying store.
     * 
     * Search for an entity of the specified class and primary key and lock it 
     * with respect to the specified lock type. JPA-LiTE will never search the
     * persistent context, this the entity will be always be obtained from the DB
     * If the lock mode type is pessimistic and the entity instance is found but 
     * cannot be locked: - the PessimisticLockException will be thrown.
     * If the database locking failure causes transaction-level rollback. - 
     * the LockTimeoutException will be thrown.
     * If the database locking failure causes only statement-level
     * rollback If a vendor-specific property or hint is not recognized, it is
     * silently ignored. 
     * 
     * @param <T>
     * @param entityClass   Java Type (class) of the entity.
     * @param primaryKey    Primary key of the record.
     * @param lockMode      Lock type
     * @param properties    Vendor specific properties
     * 
     * @return T            Record matching with the primary key specified.
     * @throws JdbcException 
     */
    public <T> T get(Class<T> entityClass
        , Object primaryKey
        , LockModeType lockMode
        , Map<String, Object> properties) throws JdbcException {
        
        T element = null;
        
        try {
            query.addEntityClass(entityClass);
            query.addPk(primaryKey);
            query.setLockModeType(lockMode);
            
            // It's a primary key query, therefore try to fetch the
            // relationship [ child record(s) ], only if the fetch type is
            // set to EAGER.
            ClassDescriptor desc = query.handler().getDescriptor(entityClass);
            
            if (desc.oneToOne() != null || desc.oneToMany() != null) {
                List<String> fetchDefs = new ArrayList<>();
                
                if (desc.oneToOne() != null) {
                    for (RelAttribute rel : desc.oneToOne()) {
                        if (rel.relation().fetch() == FetchType.EAGER) {
                            fetchDefs.add("OneToOne");
                            break;
                        }
                    }
                }
                
                if (desc.oneToMany() != null) {
                    for (RelAttribute rel : desc.oneToMany()) {
                        if (rel.relation().fetch() == FetchType.EAGER) {
                            fetchDefs.add("OneToMany");
                            break;
                        }
                    }
                }
                if (! fetchDefs.isEmpty()) {
                    query.addHint(QueryHints.FETCH_DEF, fetchDefs);
                    query.addHint(QueryHints.QUERY_TYPE, QueryCache.QueryType.SELECT_REL);
                }
                query.addHint(QueryHints.ALLOW_NATIVE_QUERY, Boolean.TRUE);
                query.addHint(QueryHints.RETRIEVAL_STRATEGY, QueryHints.RetrievalStrategy.INDEX);
                
                List<Object> records = (List)query.execute();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Selected {} record(s) from DB", records.size());
                }
                if (!records.isEmpty()) {
                    element = (T)records.get(0);
                }
            }
            else {
                List<Object> records = (List)query.execute();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Selected {} record(s) from DB", records.size());
                }
                if (!records.isEmpty()) {
                    element = (T)records.get(0);
                    query.reset();

                    // Check any OneToOne mapping
                    if (desc.oneToOne() != null) {
                        for (RelAttribute relAttr : desc.oneToOne()) {
                            Object otoRel = fetchRelationship(element, relAttr);
                            if (otoRel != null) {
                                if (LOGGER.isTraceEnabled()) {
                                    LOGGER.trace("Fetching OneToOne relationship record");
                                }
                                query.handler().set(element, relAttr.name(), otoRel);
                            }
                        }
                    }

                    // Check any OneToMany mapping
                    // TODO (XXX): Check if query.reset() is required.
                    if (desc.oneToMany() != null) {
                        for (RelAttribute relAttr : desc.oneToMany()) {
                            Object otmRel = fetchRelationship(element, relAttr);
                            if (otmRel != null) {
                                if (LOGGER.isTraceEnabled()) {
                                    LOGGER.trace("Fetching OneToMany relationship record");
                                }
                                query.handler().set(element, relAttr.name(), otmRel);
                            }
                        }
                    }
                }
            }
            return element;
        }
        catch (SQLException e) {
            throw new JdbcException(e);
        }
        finally {
            query.reset();
        }
    }
    
    /**
     * Fetch the child records from the database.
     * 
     * One entity may have <code>OneToOne</code> or <code>OneToMany</code> relationships
     * with other entities from a different table. Often it is required the fetch 
     * such child record(s) once we are fetching the parent entity (record). That
     * way the returned entity will be complete.
     * 
     * @param rel   Relationship type. E.g., OneToOne, OneToMany, etc
     * @return  Object
     * @throws SQLException 
     */
    private Object fetchRelationship(Object parent, RelAttribute rel) throws SQLException {
        if (rel != null && rel.relation().fetch() == FetchType.EAGER) {
            List<Object> params = new ArrayList<>(rel.joins().size());
            
            ClassDescriptor childDesc = PersistenceHandler.get().getDescriptor(rel.datatype());
            if (childDesc == null) {
                return null;
            }
            RelAttribute childRel = null;
            if (rel.relation().relType() == RelAttribute.RelType.OneToOne) {
                // Expect a OneToOne mapping in child entity as well with the same mappedByName
                childRel = childDesc.relation(RelAttribute.RelType.OneToOne, rel.relation().mappedBy());
            }
            else if (rel.relation().relType() == RelAttribute.RelType.OneToMany) {
                // Expect a OneToOne mapping in child entity as well with the same mappedByName
                childRel = childDesc.relation(RelAttribute.RelType.ManyToOne, rel.relation().mappedBy());
            }
            if (childRel == null) {
                return null;
            }
            for (Join join : childRel.joins()) {
                Object val = query.handler().get(parent, join.referencedColumn());
                params.add(val);
            }
            
            query.addHint(QueryHints.ALLOW_NATIVE_QUERY, Boolean.TRUE);
            query.addHint(QueryHints.RETRIEVAL_STRATEGY, QueryHints.RetrievalStrategy.INDEX);

            String sql = query.builder().selectQuery(rel.datatype(), QueryCache.QueryType.SELECT_ALL);
            sql += "\n WHERE ";
            
            int i = 0;
            for (Join join : childRel.joins()) {
                String joinColumn = join.joinColumn();
                sql += (joinColumn + " = ?");
                
                if (i < childRel.joins().size() - 1) {
                    sql += "\n   AND ";
                }
                i ++;
            }
            query.addEntityClass(rel.datatype());
            
            i = 0;
            for (; i < childRel.joins().size(); i ++) {
                Join join = childRel.joins().get(i);
                query.addParameter(new ParameterImpl(
                        join.joinColumn()
                        , i + 1
                        , params.get(i).getClass())
                    , params.get(i));
            }
            return query.execute(sql);
        }
        return null;
    }
    
    /**
     * Refresh the state of this entity with that from the underlying database.
     * 
     * @param obj       Entity whose state to be updated.
     * @param lockMode  Locking mode
     * @param properties Additional vendor properties
     */
    public void refresh(Object obj
        , LockModeType lockMode
        , Map<String, Object> properties) {
        
        Object primaryKey = PersistenceHandler.get().extractPrimaryKey(obj);
        Object latest = get(obj.getClass(), primaryKey, lockMode, properties);
        
        if (latest != null) {
            PersistenceHandler.get().copy(latest, obj);
        }
    }
    
    /**
     * Close this reader.
     */
    public void close() {
        query.reset();
    }
}
