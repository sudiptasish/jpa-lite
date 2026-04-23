package org.javalabs.jpa.query;

import org.javalabs.jpa.LiteEntityManager;
import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.EntityAttribute;
import org.javalabs.jpa.descriptor.QueryCache;
import org.javalabs.jpa.util.QueryHints;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Select Object Query class.
 * 
 * <p>
 * This class is used by the EntityManager to fetch a record by it's primary key,
 * from the underlying db.
 *
 * @author Sudiptasish Chanda
 */
public class SelectQuery extends DatabaseQuery {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectQuery.class);
    
    private final ResultSetHandler rsHandler = ResultSetHandler.newResultSetHandler();
    
    // Primary key of record to be fetched from db.
    private Object pk = null;
    
    private LockModeType lockMode;
    private Map<String, Object> properties;
    
    protected final List<Parameter> bindParams;
    protected final List<Object> bindValues;
    
    public SelectQuery(LiteEntityManager em) {
        super(em);
        
        this.bindParams = new ArrayList<>(10);
        this.bindValues = new ArrayList<>(10);
    }
    
    public SelectQuery(LiteEntityManager em
            , List<Parameter> bindParams
            , List<Object> bindValues) {
        
        super(em);
        
        this.bindParams = bindParams;
        this.bindValues = bindValues;
    }
    
    /**
     * Add the primary key of the entity to this query object.
     * @param pk 
     */
    public void addPk(Object pk) {
        this.pk = pk;
        
        ClassDescriptor desc = handler().getDescriptor(entityClass());
        int index = 1;
        for (Iterator<EntityAttribute> itr = desc.ids(); itr.hasNext(); index ++) {
            EntityAttribute attribute = itr.next();
            Object val = handler().pkGet(desc, this.pk, attribute.name());
            
            addParameter(new ParameterImpl(attribute.column()
                , index
                , attribute.datatype()), val);
        }
    }
    
    public void addParameter(Parameter param, Object value) {
        bindParams.add(param);
        bindValues.add(value);
    }
    
    /**
     * Set the lock mode type.
     * If the lock mode is set to PESSIMISTIC_READ or PESSIMISTIC_WRITE, then
     * an exclusive row level lock would be obtained.
     * 
     * @param lockMode 
     */
    public void setLockModeType(LockModeType lockMode) {
        this.lockMode = lockMode;
    }

    public LockModeType getLockModeType() {
        return lockMode;
    }
    
    /**
     * Set additional hint or other vendor level properties.
     * @param properties 
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public Object execute() throws SQLException {
        return execute(dbQuery());
    }

    @Override
    public Object execute(String query) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getEm().unwrap(java.sql.Connection.class);
            
            pstmt = conn.prepareStatement(query);
            pstmt.setFetchSize(getEm().getFetchSize());
            
            List<Object> params = prepare(pstmt);
            if (verbose()) {
                log(query, params);
            }
            rs = pstmt.executeQuery();
            pstmt.clearParameters();
            
            if (entityClass() != null) {
                return rsHandler.handleResultSet(rs, entityClass(), hints);
            }
            else {
                return rsHandler.handleResultSet(rs, hints);
            }
        }
        finally {
            hints.clear();
            
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    @Override
    protected String dbQuery() {
        QueryCache.QueryType qType = (QueryCache.QueryType)hints.get(QueryHints.QUERY_TYPE);
        if (qType != null) {
            return builder().selectQuery(entityClass(), qType);
        }
        if (lockMode == LockModeType.NONE || lockMode == null) {
            return builder().selectQuery(entityClass());
        }
        else if (lockMode == LockModeType.PESSIMISTIC_READ || lockMode == LockModeType.PESSIMISTIC_WRITE) {
            return builder().selectQuery(entityClass(), QueryCache.QueryType.SELECT_LOCK);
        }
        else {
            throw new IllegalArgumentException("Lock mode type " + lockMode + " is not supported");
        }
    }

    /**
     * Prepare the statement by substituting the placeholders with the bind variables.
     * 
     * @param pStmt     Prepared statement for the select query.
     * @return  List    Of bind variables in the order they were added.
     * @throws  SQLException 
     */
    protected List<Object> prepare(PreparedStatement pStmt) throws SQLException {
        List<Object> params = new ArrayList<>(2);
        for (int i = 0; i < bindParams.size(); i ++) {
            Parameter param = bindParams.get(i);
            Object val = bindValues.get(i);
            
            queryMechansim().prepare(pStmt
                , param.getPosition()
                , param.getParameterType()
                , val);
            params.add(val);
        }
        return params;
    }
    
    /**
     * If verbose is on, then dump the database query along with the bind parameters.
     * @param query     DB query to be executed.
     * @param params    Bind variables.
     */
    private void log(String query, List<Object> params) {
        buffer.append("\n")
            .append(query)
            .append("\n  Bind => ")
            .append(params);

        LOGGER.trace(buffer.toString());
        buffer.delete(0, buffer.length());
    }

    @Override
    public void reset() {
        super.reset();
        
        pk = null;
        bindParams.clear();
        bindValues.clear();
        buffer.delete(0, buffer.length());
    }
}
