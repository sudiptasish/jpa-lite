package org.javalabs.jpa;

import org.javalabs.jpa.descriptor.EntityAttribute;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The query mechanism interface.
 * 
 * <p>
 * Application uses third-party drivers to connect to and query a database
 * using different mechanism. Some of them are, odbc, http protocol, jdbc, etc.
 * JPA-LITE supports connection over jdbc type 4 driver, where post obtaining a
 * connection, {@link Statement}, {@link PreparedStatement}, and {@link CallableStatement}
 * objects are used for executing any SQL statement. Often one has to specify
 * the query parameters and/or bind variables while executing a statement. This
 * interface exposes the API to facilitate the same.
 *
 * @author Sudiptasish Chanda
 */
public interface QueryMechanism {
    
    /**
     * Api to prepare a {@link PreparedStatement} by adding appropriate bind variables.
     * 
     * PreparedStatement interface in JDBC API represents the precompiled statement
     * and one can create a precompiled statement by passing the query with bind 
     * variables to the prepareStatement() method of the Connection interface, or
     * use question mark(?). If using pre-compiled statement, then the question
     * mark placeholder needs to be substitued with appropriate value.
     * 
     * @param pStmt         Sql Prepared Statement object
     * @param bindIndex     Index of the parameter name in the statement buffer.        
     * @param attribute     Field definition of a db column.
     * @param bind          Value of the column to be set.
     * 
     * @throws SQLException 
     */
    void prepare(PreparedStatement pStmt
            , int bindIndex
            , EntityAttribute attribute
            , Object bind) throws SQLException;
    
    /**
     * Api to prepare a {@link PreparedStatement} by adding appropriate bind variables.
     * 
     * PreparedStatement interface in JDBC API represents the precompiled statement
     * and one can create a precompiled statement by passing the query with bind 
     * variables to the prepareStatement() method of the Connection interface, or
     * use question mark(?). If using pre-compiled statement, then the question
     * mark placeholder needs to be substitued with appropriate value.
     * 
     * @param pStmt         Sql Prepared Statement object
     * @param bindIndex     Index of the parameter name in the statement buffer.        
     * @param datatype      Data type of the column
     * @param bind          Value of the column to be set.
     * 
     * @throws SQLException 
     */
    void prepare(PreparedStatement pStmt
            , int bindIndex
            , Class<?> datatype
            , Object bind) throws SQLException;
}
