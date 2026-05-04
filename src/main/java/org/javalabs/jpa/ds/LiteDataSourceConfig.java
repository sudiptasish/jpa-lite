package org.javalabs.jpa.ds;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class LiteDataSourceConfig {

    // Size of the pool when created, and its minimum allowable size.
    private static final Integer DEFAULT_INITIAL_POOL_SIZE = 2;

    // Upper limit of size of the pool.
    private static final Integer DEFAULT_MAX_POOL_SIZE = 10;

    // Amount of time the caller (the code requesting a connection) will wait before getting a connection timeout.
    // The default is 30 seconds. A value of zero forces caller to wait indefinitely.
    private static final Integer DEFAULT_MAX_CONN_TIMEOUT_MS = 30000;       // 30s to get from pool

    // Maximum time in seconds that a connection can remain idle in the pool.
    // After this time, the pool can close this connection. This property does not control connection timeouts on the database server.
    // Keep this timeout shorter than the database server timeout.
    private static final Integer DEFAULT_MAX_IDLE_TIMEOUT_MS = 30000;       // 30s to get from pool

    // Specifies the transaction isolation level of the pooled database connections.
    private static final Integer DEFAULT_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    private static final Integer DEFAULT_IDLE_SESS_TIMEOUT_MS = 60000;       // 60s for idle session timeout

    // If true, the pool validates connections (checks to find out if they are usable) before providing them to an application.
    private static final Boolean DEFAULT_VERIFY_CONNECTION = Boolean.FALSE;

    private Integer currentSize = 0;
    private Integer maxSize = 0;
    private Integer connTimeout = 0;
    private Integer idleTimeout = 0;
    private Integer txnIsolation = 0;
    private Boolean verify = Boolean.FALSE;

    private final String url;
    private final String user;
    private String password;

    private final List<String> fatalClasses;

    public LiteDataSourceConfig(Map<String, Object> config) {
        this.url = (String) config.get("db.url");
        this.user = (String) config.get("db.user");
        this.password = (String) config.get("db.password");

        currentSize = (Integer) config.getOrDefault("db.pool.size", DEFAULT_INITIAL_POOL_SIZE);
        maxSize = (Integer) config.getOrDefault("db.pool.size", DEFAULT_MAX_POOL_SIZE);
        connTimeout = (Integer) config.getOrDefault("db.pool.conn.timeout", DEFAULT_MAX_CONN_TIMEOUT_MS);
        idleTimeout = (Integer) config.getOrDefault("db.pool.idle.timeout", DEFAULT_MAX_IDLE_TIMEOUT_MS);
        txnIsolation = (Integer) config.getOrDefault("db.pool.txn.isolation", DEFAULT_ISOLATION_LEVEL);
        verify = (Boolean) config.getOrDefault("db.pool.verify.connection", DEFAULT_VERIFY_CONNECTION);

        if (config.containsKey("db.pool.fatal.classes")) {
            String s = (String) config.get("db.pool.fatal.classes");
            String[] arr = s.split(",");
            for (int i = 0; i < arr.length; i ++) {
                arr[i] = arr[i].trim();
            }
            fatalClasses = Arrays.asList(arr);
        }
        else {
            fatalClasses = Arrays.asList(new String[] {
                "08", // connection error
                "53", // insufficient resources

                // nb: not just "57" as that includes query cancel which is nonfatal
                "57P01", // admin shutdown
                "57P02", // crash shutdown
                "57P03", // cannot connect now

                "58", // system error (backend)
                "60", // system error (driver)
                "99", // unexpected error
                "F0", // configuration file error (backend)
                "XX", // internal error (backend)
            });
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(Integer currentSize) {
        this.currentSize = currentSize;
    }

    public void incrementCurrentSize() {
        currentSize++;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getConnTimeout() {
        return connTimeout;
    }

    public void setConnTimeout(Integer connTimeout) {
        this.connTimeout = connTimeout;
    }

    public Integer getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Integer getTxnIsolation() {
        return txnIsolation;
    }

    public void setTxnIsolation(Integer txnIsolation) {
        this.txnIsolation = txnIsolation;
    }

    public Boolean getVerify() {
        return verify;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public List<String> getFatalClasses() {
        return fatalClasses;
    }
}
