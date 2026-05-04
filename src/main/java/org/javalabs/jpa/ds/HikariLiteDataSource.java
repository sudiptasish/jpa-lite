package org.javalabs.jpa.ds;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.text.MessageFormat;
import java.util.Map;
import javax.sql.DataSource;
import org.javalabs.jpa.util.ObjectCreationUtil;

/**
 *
 * @author schan280
 */
public class HikariLiteDataSource implements DataSource {
    
    private DataSource datasource;
    
    public HikariLiteDataSource(Map<String, Object> config) {
        try {
            Object hkConfig = ObjectCreationUtil.create("com.zaxxer.hikari.HikariConfig");

            if (config.get("db.url") != null) {
                String url = MessageFormat.format((String)config.get("db.url")
                    , config.get("db.host")
                    , config.get("db.port")
                    , config.get("db.schema"));

                set(hkConfig, "setJdbcUrl", url);
            }
            else if (config.get("datasource.class") != null) {
                set(hkConfig, "setDataSourceClassName", (String)config.get("datasource.class"));
            }
            else {
                throw new RuntimeException("Either of db.url or datasource.class must be specified");
            }
            set(hkConfig, "setUsername", (String)config.get("db.user"));
            set(hkConfig, "setPassword", (String)config.get("db.password"));

            set(hkConfig, "setUsername", (String)config.get("db.user"));
            set(hkConfig, "setUsername", (String)config.get("db.user"));
            set(hkConfig, "setUsername", (String)config.get("db.user"));
            
            // set(hkConfig, "addDataSourceProperty", "cachePrepStmts", "true");
            // set(hkConfig, "addDataSourceProperty", "prepStmtCacheSize", "250");
            // set(hkConfig, "addDataSourceProperty", "prepStmtCacheSqlLimit", "2048");

            // Now, set the custom properties.
            if (config.get("db.pool.name") != null) {
                set(hkConfig, "setPoolName", (String)config.get("db.pool.name"));
            }
            if (config.get("db.pool.size.max") != null) {
                set(hkConfig, "setMaximumPoolSize", (String)config.get("db.pool.size.max"));
            }
            if (config.get("db.pool.conn.timeout") != null) {
                set(hkConfig, "setConnectionTimeout", (String)config.get("db.pool.conn.timeout"));
            }
            if (config.get("db.pool.min.idle") != null) {
                set(hkConfig, "setMinimumIdle", Integer.valueOf((String)config.get("db.pool.min.idle")));
            }
            if (config.get("db.pool.idle.timeout") != null) {
                set(hkConfig, "setIdleTimeout", Integer.valueOf((String)config.get("db.pool.idle.timeout")));
            }
            if (config.get("db.pool.keep.alive") != null) {
                set(hkConfig, "setKeepaliveTime", Integer.valueOf((String)config.get("db.pool.keep.alive")));
            }
            set(hkConfig, "setRegisterMbeans", true);

            datasource =(DataSource) ObjectCreationUtil.create(
                    "com.zaxxer.hikari.HikariDataSource"
                    , new Class[] {hkConfig.getClass()}
                    , new Object[] {hkConfig});
        
        }
        catch (RuntimeException | ReflectiveOperationException e) {
            if (e instanceof RuntimeException && e.getCause() instanceof ClassNotFoundException) {
                // hikari library is not in classpath. Therefore continue with platform data source;
                throw new RuntimeException(e.getCause());
            }
            else {
                throw new RuntimeException(e);
            }
        }
    }
    
    private void set(Object hkConfig, String methodName, Object... params) throws ReflectiveOperationException {
        Class[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i ++) {
            classes[i] = params[i].getClass() == Boolean.class ? boolean.class : params[i].getClass();
        }
        Method method = hkConfig.getClass().getDeclaredMethod(methodName, classes);
        method.invoke(hkConfig, params);
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
