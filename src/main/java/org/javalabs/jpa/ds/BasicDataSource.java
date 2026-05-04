package org.javalabs.jpa.ds;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import javax.sql.DataSource;

/**
 * A basic framework provided data source.
 * 
 * <p>
 * The basic data source uses simple jdbc based connection mechanism. It does not maintain any pool.
 *
 * @author Sudiptasish Chanda
 */
public class BasicDataSource implements DataSource {
    
    private final DataSource datasource;
    
    public BasicDataSource(Map<String, Object> config) {
        datasource = new DriverManagerDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return datasource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return datasource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        datasource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        datasource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return datasource.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return datasource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
