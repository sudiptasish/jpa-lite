package org.javalabs.jpa;

import org.javalabs.jpa.query.DeleteQuery;
import org.javalabs.jpa.query.InsertQuery;
import org.javalabs.jpa.query.UpdateQuery;
import org.javalabs.jpa.query.WriteQuery;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC writer class.
 * 
 * The Writer class responsible for any kind of CRUD operation. It internally
 * maintains different query object to perform each of the operation (create,
 * update, delete). In addition to that it also supports stored procedure call.
 * 
 * This class is the perfect choice for employing any batching strategy. Every
 * invocation of the EntityManager.persist method will actually store the entities
 * in the memory until the in-memory object count reaches a certain threshold (as
 * specified in persistence.xml), post which, they will be dispatched to DB.
 *
 * @author Sudiptasish Chanda
 */
public class JdbcWriter {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcWriter.class);
    
    // Underlying entity manager.
    private final LiteEntityManager em;
    
    // Position:
    // 0 -> InsertQuery
    // 1 -> UpdateQuery
    // 2 -> DeleteQuery
    private final WriteQuery[] queries;
    
    JdbcWriter(LiteEntityManager em) {
        this.em = em;
        queries = new WriteQuery[] {new InsertQuery(em)
            , new UpdateQuery(em)
            , new DeleteQuery(em)};
    }

    /**
     * Return the underlying entity manager, this writer is associated with.
     * @return LiteEntityManager
     */
    public LiteEntityManager getEm() {
        return em;
    }
    
    /**
     * Accept a request to insert a new entity.
     * 
     * @param entity    Entity to be inserted.
     * @throws JdbcException 
     */
    public void add(Object entity) throws JdbcException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Adding a new entity {} to the insert list", entity);
        }
        checkPrepare(queries[0], entity);
        queries[0].addEntityClass(entity.getClass());
        queries[0].add(entity);
    }
    
    /**
     * Accept a request to update an existing entity.
     * 
     * @param entity    Entity to be updated.
     * @throws JdbcException 
     */
    public void update(Object entity) throws JdbcException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Adding an entity {} to the update list", entity);
        }
        checkPrepare(queries[1], entity);
        queries[1].addEntityClass(entity.getClass());
        queries[1].add(entity);
    }
    
    /**
     * Accept a request to delete an existing entity.
     * 
     * @param entity    Entity to be updated.
     * @throws JdbcException 
     */
    public void delete(Object entity) throws JdbcException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Adding an entity {} to the delete list", entity);
        }
        checkPrepare(queries[2], entity);
        queries[2].addEntityClass(entity.getClass());
        queries[2].add(entity);
    }
    
    /**
     * Before adding the entity to the list of transactional entities, check if the
     * existing list is eligible for dispatch.
     * 
     * @param dbQuery   Database query
     * @param entity    Managed entity being added
     * @throws JdbcException 
     */
    private void checkPrepare(WriteQuery dbQuery, Object entity) throws JdbcException {
        checkPrepare(dbQuery, entity, false);
    }
    
    /**
     * Before adding the entity to the list of transactional entities, check if the
     * list is eligible for dispatch.
     * This would be done by checking the size of list,
     * and comparing it with the jdbc batch size. If the list size exceeds the batch
     * writing size, dispatch the list to the database.
     * 
     * Also a DatabaseQuery holds a set of homogenous objects, if user tries to add
     * a different type, then the existing set of (homogenous) objects will be dispatched,
     * before accepting a new type.
     * 
     * @param dbQuery       Database query against which the check will be made.
     * @param entity        Entity being added
     * @param force         If set to true, dispatch the pending objects to db.
     * @throws JdbcException 
     */
    private void checkPrepare(WriteQuery dbQuery, Object entity, boolean force) throws JdbcException {
        // Now check if the current entity has any relationship.
        
        if (force || dbQuery.recordCount() >= getEm().getBatchSize()
                  || (dbQuery.entityClass() != null && dbQuery.entityClass() != entity.getClass())) {
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("checkPrepare::Dispatching transactional objects to DB");
            }
            writeToDb();
        }
    }
    
    /**
     * Check if a pre-query flush is required.
     * 
     * Sometimes a select may occur in the middle of a transaction.
     * E.g., Some application component C1, has called merge() operation n times
     * on same entity, which would result n different objects to be cached with
     * the entity manager (until a flush is issued). Now before doing a next merge(),
     * component C1 does a find on the same entity type, in which case the existing
     * data won't be flushed. If the find (select) is on a different entity, then
     * the existing to-be-updated/to-be-inserted/to-be-deleted data has to be dispatched
     * to database so as to ensure the records are available for subsequent select.
     * 
     * @param type
     * @return boolean
     */
    public boolean shouldFlush(Class<?> type) {
        for (WriteQuery dbQuery : queries) {
            if (dbQuery.hasItem() && !dbQuery.entityClass().isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Write the changes to db.
     * If there is a changeset to be written to the db, then this API will
     * return true, indicating something has been written to DB.
     * 
     * @return boolean
     * @throws JdbcException
     */
    public boolean writeToDb() throws JdbcException {
        boolean written = false;
        
        try {
            for (WriteQuery dbQuery : queries) {
                if (!dbQuery.hasItem()) {
                    continue;
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Batch Size for LiTE EM is {}. Record(s) being dispatched by {} is: {}"
                            , getEm().getBatchSize()
                            , dbQuery.getClass().getSimpleName()
                            , dbQuery.recordCount());
                }
                writeToDb(dbQuery);
                written = true;
                dbQuery.reset();
            }
        }
        catch (SQLException e) {
            written = false;
            throw new JdbcException(e.getMessage(), e);
        }
        finally {
            if (!written) {
                for (WriteQuery dbQuery : queries) {
                    dbQuery.reset();
                }
            }
        }
        return written;
    }
    
    /**
     * Write the changeset to underlying database.
     * @param dbQuery
     * @throws SQLException
     */
    public void writeToDb(WriteQuery dbQuery) throws SQLException {
        dbQuery.execute();
    }
    
    /**
     * Close this reader.
     */
    public void close() {
        for (WriteQuery dbQuery : queries) {
            dbQuery.reset();
        }
    }
}
