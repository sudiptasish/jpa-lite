package org.javalabs.jpa.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data source provided by jpa-lite.
 *
 * @author schan280
 */
public class JpaLiteDataSource implements DataSource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaLiteDataSource.class);
    private PrintWriter pw;
    
    private final Map<String, Object> config = new HashMap<>();
    
    public JpaLiteDataSource() {
        this.pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new ByteArrayOutputStream())));
    }
    
    public void init(Map<String, Object> config) {
        try {
            Objects.requireNonNull(config, "Configuration object cannot be null or empty");
            Objects.requireNonNull(config.get("db.url"), "Must provide db.url property");
            Objects.requireNonNull(config.get("db.user"), "Must provide db.user property");
            Objects.requireNonNull(config.get("db.password"), "Must provide db.password property");
            // Objects.requireNonNull(config.get("db.driver"), "Must provide db.driver property");

            this.config.putAll(config);

            // Ideally the url should have a placeholder for host, port and schema/sid name.
            // If there is no placeholder, the below method will be a no-op (non-breaking change).
            String url = MessageFormat.format((String)this.config.get("db.url")
                , this.config.get("db.host")
                , this.config.get("db.port")
                , this.config.get("db.schema"));

            this.config.put("db.url", url);
            
            // Extract the driver class.
            String driver = (String)this.config.get("db.driver");
            if (driver != null) {
                Class.forName(driver);
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Initialized jpa-lite data source. DB Url: {}, Driver: {}"
                        , this.config.get("db.url")
                        , (this.config.get("db.driver") != null ? this.config.get("db.driver") : "NA"));
            }
        }
        catch (ClassNotFoundException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Map<String, Object> config() {
        return Collections.unmodifiableMap(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                (String) config.get("db.url")
                , (String) config.get("db.user")
                , (String) config.get("db.password"));
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(
                (String) config.get("db.url")
                , username
                , password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return pw;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.pw = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.config.put("db.login.timeout", seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return (Integer)this.config.get("db.login.timeout");
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
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
