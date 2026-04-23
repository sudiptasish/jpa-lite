package org.javalabs.jpa.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jdbc Result set Handler.
 * 
 * This class has the APIs to fetch the records from the underlying result set and
 * populate the respective attributes of the entity class (if provided), otherwise
 * it returns a list of object array that represents the table data.
 *
 * @author Sudiptasish Chanda
 */
public abstract class ResultSetHandler {
    
    Map<String, Object> EMPTY_HINTS = new HashMap<>();
    
    protected ResultSetHandler() {}
	
    /**
     * Return a new instance of ResultSetHandler
     * @return	ResultSetHandler
     */
    public static ResultSetHandler newResultSetHandler() {
        return new DBResultSetHandler();
    }

    /**
     * Iterate through the result set and return the record object list.
     * The returned list is a two-dimensional array, where each row will represent
     * the corresponding record in the db table.
     * 
     * @param 	resultSet
     * @param   hints
     * @return	List
     * @throws  SQLException
     */
    public abstract List<Object> handleResultSet(ResultSet resultSet, Map<String, Object> hints) throws SQLException;

    /**
     * Iterate through the result set and return the record object list.
     * This will add pagination technique, that means in each cycle it will process
     * the number of records as specified by the limit parameter.
     * The returned list is a two-dimensional array, where each row will represent
     * the corresponding record in the db table.
     * 
     * @param 	resultSet
     * @param   hints
     * @param	limit
     * 
     * @return	List
     * @throws java.sql.SQLException
     */
    public abstract List<Object> handleResultSet(ResultSet resultSet
            , Map<String, Object> hints
            , int limit) throws SQLException;

    /**
     * Iterate through the result set and create the value object.
     * The attribute of the value object will be populated with the specific column value
     * taken from resultset.
     * 
     * @param <T>
     * @param 	resultSet
     * @param 	clazz
     * @return	List
     * @throws java.sql.SQLException
     */
    public abstract <T> List<T> handleResultSet(ResultSet resultSet, Class<T> clazz) throws SQLException;
    
    /**
     * 
     * @param <T>
     * @param resultSet
     * @param clazz
     * @param hints
     * @return
     * @throws SQLException 
     */
    public abstract <T> List<T> handleResultSet(ResultSet resultSet
        , Class<T> clazz
        , Map<String, Object> hints) throws SQLException;

    /**
     * Iterate through the result set and create the value object.
     * The attribute of the value object will be populated with the specific column value
     * taken from resultset.
     * This will add pagination technique, that means in each cycle it will process
     * the number of records as specified by the limit parameter.
     * 
     * @param 	resultSet
     * @param 	clazz
     * @param	limit
     * @return	List
     * @throws  SQLException
     */
    public abstract <T> List<T> handleResultSet(ResultSet resultSet
            , Class<T> clazz
            , int limit) throws SQLException;

    /**
     * 
     * @param <T>
     * @param resultSet
     * @param clazz
     * @param limit
     * @param hints
     * @return
     * @throws SQLException 
     */
    public abstract <T> List<T> handleResultSet(ResultSet resultSet
        , Class<T> clazz
        , int limit
        , Map<String, Object> hints) throws SQLException;
}