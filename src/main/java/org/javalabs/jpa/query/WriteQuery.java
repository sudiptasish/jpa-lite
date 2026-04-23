package org.javalabs.jpa.query;

import org.javalabs.jpa.LiteEntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Indicates a database write query.
 * 
 * <p>
 * All kind insert/update/delete queries belong to this category. This abstract
 * class defines the common methods of all kind of writable queries.
 *
 * @author Sudiptasish Chanda
 */
public abstract class WriteQuery extends DatabaseQuery {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteQuery.class);

    // For optimum performaance, it is always advisable to batch write queries.
    // This parameter stores the batch size.
    private final int batch;
    
    // List to hold the entities to be written to DB.
    // The maximum size of the list will be determined by the batch size.
    protected final List<Object> entities;
    
    WriteQuery(LiteEntityManager em) {
        super(em);
        this.batch = em.getBatchSize();
        this.entities = new ArrayList<>(this.batch);
    }

    /**
     * Return the batch size.
     * @return int
     */
    public int batchSize() {
        return batch;
    }
    
    /**
     * Add the new record to in-memory store, before they are actually persisted.
     * @param entity 
     */
    public void add(Object entity) {
        this.entities.add(entity);
    }
    
    /**
     * Return the number of records present in this query.
     * @return int
     */
    public int recordCount() {
        return entities.size();
    }
    
    /**
     * Check if this write query has any item to be dispatched to db.
     * @return boolean
     */
    public boolean hasItem() {
        return !entities.isEmpty();
    }

    @Override
    public Object execute() throws SQLException {
        return execute(dbQuery());
    }

    @Override
    public Object execute(String query) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getEm().unwrap(java.sql.Connection.class);
            
            // For insert query, the framework will check if the primary key column
            // is set to be auto-generated. In which case, it will make a best attemp
            // to fetch the auto-generated keys.
            pstmt = statemnt(conn, query);
            
            // Facilitate batching only if the list has more than one element.
            List<List<Object>> params = prepare(pstmt);
            if (verbose()) {
                log(query, params);
            }
            int[] result = pstmt.executeBatch();
            
            // Before returning the result, populate the respective columns of the
            // entiies with the generated values.
            retrieveGeneratedId(pstmt);
            
            return result;
        }
        finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }
    
    protected PreparedStatement statemnt(Connection conn, String query) throws SQLException {
        return conn.prepareStatement(query);
    }
    
    /**
     * If the statement is setup to retrieve the generated id values, then this
     * method can be used to extract the id values from the <code>pstmt</code>.
     * 
     * @param pstmt
     * @throws SQLException 
     */
    protected void retrieveGeneratedId(PreparedStatement pstmt) throws SQLException {
        // Empty Implementation
    }
    
    /**
     * Prepare the statement.
     * 
     * Preparing the statement essentially means, replacing the question mark/
     * place holders with the bind variables. Different type of queries have their
     * own strategy of replacing the bind variables. This API will iterate through
     * the the entities added so far to extract the column values. After successful
     * replace, this API will return the list containing the bind variables.
     * 
     * @param pStmt     Current prepared statement being executed.
     * @return List
     * @throws SQLException 
     */
    protected abstract List<List<Object>> prepare(PreparedStatement pStmt) throws SQLException;
    
    /**
     * If verbose is on, then dump the database query along with the bind parameters.
     * @param query     DB query to be executed.
     * @param params    Bind variables.
     */
    private void log(String query, List<List<Object>> params) {
        buffer.append("\n")
            .append(query)
            .append("\n  Bind => [");
        
        for (List<Object> bind : params) {
            buffer.append("\n            ")
                .append(bind);
        }
        buffer.append("\n  ").append("]");

        LOGGER.trace(buffer.toString());
        buffer.delete(0, buffer.length());
    }

    @Override
    public void reset() {
        entities.clear();
        super.reset();
    }
}
