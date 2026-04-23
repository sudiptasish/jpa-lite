package org.javalabs.jpa.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic framework provided data source.
 * 
 * <p>
 * The basic data source uses simple jdbc based connection mechanism. It does not maintain any pool.
 *
 * @author Sudiptasish Chanda
 */
public class BasicDataSource implements PoolDataSource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataSource.class);
    
    private boolean initialized = false;
    
    private final DataSource dataSource;
    
    public BasicDataSource(Map<String, Object> config) {
        dataSource = new JpaLiteDataSource();
        ((JpaLiteDataSource)dataSource).init(config);
    }
    
    @Override
    public void init() {
        if (initialized) {
            throw new IllegalStateException("BasicDataSource is already initialized");
        }
        initialized = true;
    }

    @Override
    public String getDSProperty(String name) {
        return (String) ((JpaLiteDataSource)dataSource).config().get(name);
    }

    @Override
    public Connection getCachedConnection() throws SQLException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Requesting new database connection");
        }
        Connection connection = dataSource.getConnection();
        return connection;
    }

    @Override
    public void closeCachedConnection(Connection connection) throws SQLException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Releasing old database connection");
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void closeDS() {
        // Do nothing. Connections are never cached.
    }
}
