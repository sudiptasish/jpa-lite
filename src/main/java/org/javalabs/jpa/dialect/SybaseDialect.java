package org.javalabs.jpa.dialect;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class SybaseDialect extends AbstractDBDialect {
    
    private static final Map<Class<?>, String> DATATYPE_MAPPING = new HashMap<>();
    
    static {
        DATATYPE_MAPPING.put(byte[].class, "BLOB");
        DATATYPE_MAPPING.put(String.class, "VARCHAR");
        DATATYPE_MAPPING.put(Boolean.class, "SMALLINT");
        DATATYPE_MAPPING.put(Byte.class, "SMALLINT");
        DATATYPE_MAPPING.put(Short.class, "INTEGER");
        DATATYPE_MAPPING.put(Integer.class, "INTEGER");
        DATATYPE_MAPPING.put(Long.class, "BIGINT");
        DATATYPE_MAPPING.put(Float.class, "DECIMAL(10, 2)");
        DATATYPE_MAPPING.put(Double.class, "DECIMAL(10, 2)");
        DATATYPE_MAPPING.put(BigDecimal.class, "DECIMAL(20, 6)");
        DATATYPE_MAPPING.put(Timestamp.class, "DATETIME");
        DATATYPE_MAPPING.put(Date.class, "DATETIME");
        DATATYPE_MAPPING.put(Time.class, "DATETIME");
    }
    
    public SybaseDialect() {
        super();
    }

    @Override
    public String data_type(Class<?> javatype) {
        String dbDataType = DATATYPE_MAPPING.get(javatype);
        if (dbDataType == null) {
            throw new IllegalArgumentException("No sybase data type found for java type: " + javatype.getSimpleName());
        }
        return dbDataType;
    }

    @Override
    public String db_url(String host, String port, String db) {
        return "jdbc:sybase:Tds:" + host + ":" + port + "/" + db;
    }

    @Override
    public String run_command(String sqlFile) {
        return "-- source " + sqlFile + " --";
    }

    @Override
    public String query_tables(String schema) {
        return "SELECT table_name FROM sys.systable";
    }

    @Override
    protected String auto_gen_data_type() {
        return "";
    }

    @Override
    public String timestamp() {
        return "SELECT CURRENT_TIMESTAMP";
    }

    @Override
    public String version() {
        return "SELECT CURRENT_TIMESTAMP, @@version";
    }

    @Override
    public String db_table_metadata(EntityManager em, Map<String, Object> props) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Class<?>> metadataClasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
