package org.javalabs.jpa.dialect;

/**
 * Defines a contract for database dialects.
 * 
 * <p>
 * Provides methods for handling database-specific SQL syntax,
 * functions, and capabilities.
 *
 * @author Sudiptasish Chanda
 */
public enum Dialect {
    
    DB2 ("org.javalabs.jpa.dialect.DB2Dialect"),
    DERBY ("org.javalabs.jpa.dialect.DerbyDialect"),
    H2 ("org.javalabs.jpa.dialect.H2Dialect"),
    MYSQL ("org.javalabs.jpa.dialect.MySqlDialect"),
    ORACLE ("org.javalabs.jpa.dialect.OracleDialect"),
    POSTGRES ("org.javalabs.jpa.dialect.PostgresDialect"),
    SYBASE ("org.javalabs.jpa.dialect.SybaseDialect");
    
    private final String dialectClass;
    
    Dialect(String dialectClass) {
        this.dialectClass = dialectClass;
    }
    
    public String dialectClass() {
        return dialectClass;
    }
}
