package org.javalabs.jpa.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface to represent a datasource object.
 * 
 * <p>
 * JPA-LiTE offers sourcing connection from a local data source even if the 
 * transaction type is specified as <code>RESOURCE_LOCAL</code>.
 * Typically if the transaction type of the persistence unit is JTA, the jta-datasource
 * element is used to declare the JNDI name of the JTA data source that will be used
 * to obtain connections. This is the common case.
 * 
 * <p>
 * It is possible for some application to set <code>RESOURCE_LOCAL</code> as
 * transaction type, yet use a datasource for connection pooling. If the datasource
 * option is enabled, via <code>jpa-lite.data.source</code>, then the custom
 * datasource will be initialized and framework will start connection pooling.
 * However, to use this option, application has to implement the {@link PoolDataSource}
 * class and override the methods defined. If no custom data source is provided,
 * then the platform {@link BasicDataSource} will be used. This datasource does
 * not offer connection pooling.
 *
 * @author Sudiptasish Chanda
 */
public interface PoolDataSource {
    
    /**
     * Initialize the data source.
     */
    void init();
    
    /**
     * Return the property for this key.
     * @param key
     * @return String
     */
    String getDSProperty(String key);
    
    /**
     * Return a pooled connection from the underlying data source.
     * @return Connection
     * @throws SQLException
     */
    Connection getCachedConnection() throws SQLException;
    
    /**
     * Close and return the connection to the underlying pool.
     * @param connection 
     * @throws SQLException 
     */
    void closeCachedConnection(Connection connection) throws SQLException;
    
    /**
     * Close this data source.
     */
    void closeDS();
} 
