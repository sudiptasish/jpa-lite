package org.javalabs.jpa.query;

import org.javalabs.jpa.JdbcException;
import org.javalabs.jpa.LiteEntityManager;
import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.util.QueryHints;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Parameter;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.Statement;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Timeout;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.Type;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of SelectObjectQuery and JPA TypedQuery.
 * 
 * This class supports native JDBC query via NativeEntityManager.
 * It is responsible for executing any NamedNative query that a JPA entity defines.
 * This query supports pagination via offset and limit attributes.
 * 
 * @author Sudiptasish Chanda
 */
public class NativeJdbcQuery extends SelectQuery implements TypedQuery {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeJdbcQuery.class);

    // The raw jdbc query
    private final String query;
    
    // Start position of the result set, if offset is provided.
    private int offset = -1;
    
    // Maximum number of results to be retrieved, if limit is provided.
    private int limit = -1;
    
    // Indicates whether the SQL query has any collection object as bind parameter.
    // A collection object will be passed only if the query has an IN clause.
    private List<Integer> collectionIndices = new ArrayList<>();
    
    private FlushModeType flushMode = FlushModeType.AUTO;
    
    private ClassDescriptor desc = null;
    
    public NativeJdbcQuery(LiteEntityManager em
        , String query
        , Class<?> entityClass) {
        
        super(em);
        this.query = query;
        
        if (entityClass != null) {
            addEntityClass(entityClass);
            this.desc = PersistenceHandler.get().getDescriptor(entityClass);
        }
    }

    @Override
    public List getResultList() {
        try {
            return (List)execute();
        }
        catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public Object getSingleResult() {
        List list = getResultList();
        
        if (list.isEmpty()) {
            throw new NoResultException("No matching record found");
        }
        if (list.size() > 1) {
            throw new JdbcException("More than one row found");
        }
        if (list.get(0) instanceof Object[]) {
            Object[] _arr = (Object[])list.get(0);
            if (_arr.length == 1) {
                // Select query has a single column.
                return _arr[0];
            }
            return _arr;
        }
        else {
            return list.get(0);
        }
    }

    @Override
    public int executeUpdate() {
        getEm().verifyTxn();
        
        // Execute the raw jdbc write query.
        Connection conn = null;
        PreparedStatement pStmt = null;
        
        try {
            conn = getEm().unwrap(java.sql.Connection.class);
            pStmt = conn.prepareStatement(dbQuery());
            List<Object> params = prepare(pStmt);
            
            if (verbose()) {
                log(query, params);
            }
            
            return pStmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new JdbcException(e);
        }
        finally {
            try {
                if (pStmt != null) {
                    pStmt.close();
                }
            }
            catch (SQLException e) {
                // Do Nothing.
            }
        }
    }

    @Override
    protected String dbQuery() {
        String q = query;
        
        // If the query has any IN clause, then we need to flatten the query.
        if (! collectionIndices.isEmpty()) {
            StringBuilder builder = new StringBuilder(32);
            
            for (Integer collectionIndex : collectionIndices) {
                Parameter p = bindParams.get(collectionIndex);

                int startIdx = q.indexOf(":" + p.getName());
                if (startIdx <= 0) {
                    throw new IllegalArgumentException("No bind parameter name [" + p.getName() + "] found");
                }

                Object bindVal = bindValues.get(collectionIndex);
                int size = ((Collection)bindVal).size();

                for (byte i = 0; i < size; i ++) {
                    builder.append("?");
                    if (i < size - 1) {
                        builder.append(", ");
                    }
                }
                q = q.replace(":" + p.getName(), builder.toString());
                builder.delete(0, builder.length());
            }
        }
        if (offset != -1 && limit != -1) {
            q += " LIMIT " + limit + " OFFSET " + offset;
        }
        return q;
    }

    @Override
    protected List<Object> prepare(PreparedStatement pStmt) throws SQLException {
        if (hints.get(QueryHints.ENABLE_BATCH) != null
                && hints.get(QueryHints.ENABLE_BATCH).equals("true")) {

            return prepareBatchSql(pStmt);
        }
        else {
            return prepareSimpleSql(pStmt);
        }
    }
    
    private List<Object> prepareBatchSql(PreparedStatement pStmt) throws SQLException {
        Object bindVal = bindValues.get(0);
        
        if (! bindVal.getClass().isArray()) {
            throw new IllegalArgumentException("Hint " + QueryHints.ENABLE_BATCH + " is true,"
                    + " but non-array element is provided.");
        }
        
        int length = ((Object[])bindVal).length;
        Parameter p = null;
        List<Object> params = new ArrayList<>(length);
        List<Object> binds = new ArrayList<>(bindParams.size());
        
        for (short j = 0; j < length; j ++) {
            for (short i = 0; i < bindParams.size(); i ++) {
                p = bindParams.get(i);
                Object[] vals = (Object[])bindValues.get(i);    // This is an array

                queryMechansim().prepare(pStmt
                    , p.getPosition()
                    , p.getParameterType()
                    , vals[j]);
                
                binds.add(vals[j]);
                
                if (verbose()) {
                    binds.add(vals[j]);
                }
            }
            pStmt.addBatch();
            
            if (verbose()) {
                params.add(binds);
            }
        }
        return params;
    }
    
    private List<Object> prepareSimpleSql(PreparedStatement pStmt) throws SQLException {
        Parameter p = null;
        Object bindVal = null;
        
        List<Object> params = new ArrayList<>(1);
        List<Object> binds = new ArrayList<>(bindParams.size());
        
        for (short i = 0; i < bindParams.size(); i ++) {
            p = bindParams.get(i);
            bindVal = bindValues.get(i);
            
            if (Collection.class.isAssignableFrom(bindVal.getClass())) {
                int index = p.getPosition();
                for (Iterator<Object> itr = ((Collection)bindVal).iterator(); itr.hasNext(); ) {
                    Object obj = itr.next();
                    queryMechansim().prepare(pStmt
                        , index ++
                        , p.getParameterType()
                        , obj);
                }
            }
            else {
                queryMechansim().prepare(pStmt
                    , p.getPosition()
                    , p.getParameterType()
                    , bindVal);
            }   
            if (verbose()) {
                binds.add(bindVal);
            }
        }
        if (verbose()) {
            params.add(binds);
        }
        return params;
    }

    @Override
    public TypedQuery setFirstResult(int position) {
        offset = position;
        return this;
    }

    @Override
    public TypedQuery setMaxResults(int position) {
        limit = position;
        return this;
    }

    @Override
    public TypedQuery setHint(String hintName, Object value) {
        hints.put(hintName, value);
        return this;
    }

    @Override
    public TypedQuery setParameter(Parameter param, Object value) {
        if (Collection.class.isAssignableFrom(value.getClass())) {
            collectionIndices.add(param.getPosition() - 1);
        }
        bindParams.add(param);
        bindValues.add(value);
        return this;
    }

    @Override
    public TypedQuery setParameter(Parameter param, Calendar value, TemporalType temporalType) {
        bindParams.add(param);
        
        Object val = value;
        if (TemporalType.DATE == temporalType) {
            // Chop off the time part.
            value.set(Calendar.HOUR_OF_DAY, 0);
            value.set(Calendar.MINUTE, 0);
            value.set(Calendar.SECOND, 0);
            value.set(Calendar.MILLISECOND, 0);
            
            val = new java.sql.Date(value.getTimeInMillis());
        }
        else if (TemporalType.TIME == temporalType) {
            val = new Time(value.getTimeInMillis());
        }
        else if (TemporalType.TIMESTAMP == temporalType) {
            val = new Timestamp(value.getTimeInMillis());
        }
        bindValues.add(val);
        return this;
    }

    @Override
    public TypedQuery setParameter(Parameter param, Date value, TemporalType temporalType) {
        bindParams.add(param);
        
        Object val = value;
        if (TemporalType.DATE == temporalType) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(value);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            val = new java.sql.Date(cal.getTimeInMillis());
        }
        else if (TemporalType.TIME == temporalType) {
            val = new Time(value.getTime());
        }
        else if (TemporalType.TIMESTAMP == temporalType) {
            val = new Timestamp(value.getTime());
        }
        bindValues.add(val);
        return this;
    }

    @Override
    public TypedQuery setParameter(String name, Object value) {
        Objects.requireNonNull(value);
        Class<?> type = null;
        
        if (value.getClass().isArray()) {
            if (Array.getLength(value) == 0) {
                throw new IllegalArgumentException("Length of array cannot be empty");
            }
            type = value.getClass().getComponentType();
        }
        else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection coll = (Collection)value;
            Iterator<?> itr = coll.iterator();
            if (! itr.hasNext()) {
                throw new IllegalArgumentException("Length of collection cannot be empty");
            }
            type = itr.next().getClass();
        }
        else {
            type = value.getClass();
        }
        return setParameter(new ParameterImpl(name, bindParams.size() + 1, type), value);
    }

    @Override
    public TypedQuery setParameter(String name, Calendar value, TemporalType temporalType) {
        Objects.requireNonNull(value);
        Class<?> type = null;
        
        if (temporalType == TemporalType.DATE) {
            type = Date.class;
        }
        else if (temporalType == TemporalType.TIMESTAMP) {
            type = Timestamp.class;
        }
        else if (temporalType == TemporalType.TIME) {
            type = Time.class;
        }
        return setParameter(new ParameterImpl(name
            , bindParams.size() + 1
            , type), value, temporalType);
    }

    @Override
    public TypedQuery setParameter(String name, Date value, TemporalType temporalType) {
        Objects.requireNonNull(value);
        
        return setParameter(new ParameterImpl(name
            , bindParams.size() + 1
            , Date.class), value, temporalType);
    }

    @Override
    public TypedQuery setParameter(int position, Object value) {
        if (value.getClass().isArray() || Collection.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Bind valriable cannot be a array/collection");
        }
        return setParameter(new ParameterImpl("", position, value.getClass()), value);
    }

    @Override
    public TypedQuery setParameter(int position, Calendar value, TemporalType temporalType) {
        Class<?> type = null;
        if (temporalType == TemporalType.DATE) {
            type = Date.class;
        }
        else if (temporalType == TemporalType.TIMESTAMP) {
            type = Timestamp.class;
        }
        else if (temporalType == TemporalType.TIME) {
            type = Time.class;
        }
        return setParameter(new ParameterImpl(""
            , position
            , type), value, temporalType);
    }

    @Override
    public TypedQuery setParameter(int position, Date value, TemporalType temporalType) {
        return setParameter(new ParameterImpl(""
            , position
            , Date.class), value, temporalType);
    }

    @Override
    public TypedQuery setFlushMode(FlushModeType flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    @Override
    public int getMaxResults() {
        return limit;
    }

    @Override
    public int getFirstResult() {
        return offset;
    }

    @Override
    public Map<String, Object> getHints() {
        return hints;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return new HashSet<>((Collection)bindParams);
    }

    @Override
    public Parameter<?> getParameter(String name) {
        for (Parameter p : bindParams) {
            if (name.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        for (Parameter p : bindParams) {
            if (name.equals(p.getName()) && type == p.getParameterType()) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Parameter<?> getParameter(int position) {
        for (Parameter p : bindParams) {
            if (position == p.getPosition()) {
                return p;
            }
        }
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        for (Parameter p : bindParams) {
            if (position == p.getPosition() && type == p.getParameterType()) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        for (Parameter p : bindParams) {
            if (param.getName().equals(p.getName()) || param.getPosition() == p.getPosition()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        Parameter p = null;
        for (byte i = 0; i < bindParams.size(); i ++) {
            p = bindParams.get(i);
            if (param.getName().equals(p.getName()) || param.getPosition() == p.getPosition()) {
                return (T)bindValues.get(i);
            }
        }
        return null;
    }

    @Override
    public Object getParameterValue(String name) {
        Parameter p = null;
        for (byte i = 0; i < bindParams.size(); i ++) {
            p = bindParams.get(i);
            if (name.equals(p.getName())) {
                return bindValues.get(i);
            }
        }
        return null;
    }

    @Override
    public Object getParameterValue(int position) {
        Parameter p = null;
        for (byte i = 0; i < bindParams.size(); i ++) {
            p = bindParams.get(i);
            if (position == p.getPosition()) {
                return bindValues.get(i);
            }
        }
        return null;
    }

    @Override
    public FlushModeType getFlushMode() {
        return flushMode;
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public TypedQuery setLockMode(LockModeType lockMode) {
        super.setLockModeType(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return super.getLockModeType();
    }
    
    /**
     * If verbose is on, then dump the database query along with the bind parameters.
     * @param query     DB query to be executed.
     * @param params    Bind variables.
     */
    private void log(String query, List<Object> params) {
        buffer.append("\n")
            .append(query)
            .append("\n  Bind => [");
        
        for (Object bind : params) {
            buffer.append("\n            ")
                .append(bind);
        }
        buffer.append("\n  ").append("]");

        LOGGER.trace(buffer.toString());
        buffer.delete(0, buffer.length());
    }

    @Override
    public long getResultCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getSingleResultOrNull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setEntityGraph(EntityGraph entityGraph) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityGraph getEntityGraph() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setParameter(String name, Object value, Class type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setParameter(String name, Object value, Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setConvertedParameter(String name, Object value, Class converter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setParameter(int position, Object value, Class type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setParameter(int position, Object value, Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setConvertedParameter(int position, Object value, Class converter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setLockScope(PessimisticLockScope lockScope) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PessimisticLockScope getLockScope() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setCacheStoreMode(CacheStoreMode cacheStoreMode) {
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
    public TypedQuery setTimeout(Integer timeout) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypedQuery setTimeout(Timeout timeout) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement asStatement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <R> TypedQuery<R> ofType(Class<R> resultType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <R> TypedQuery<R> withEntityGraph(EntityGraph<R> graph) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getTimeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
