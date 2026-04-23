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
public class OracleDialect extends AbstractDBDialect {
    
    private static final Map<Class<?>, String> DATATYPE_MAPPING = new HashMap<>();
    
    static {
        DATATYPE_MAPPING.put(byte[].class, "BLOB");
        DATATYPE_MAPPING.put(String.class, "VARCHAR2");
        DATATYPE_MAPPING.put(Boolean.class, "NUMBER(1)");
        DATATYPE_MAPPING.put(Byte.class, "NUMBER(8)");
        DATATYPE_MAPPING.put(Short.class, "NUMBER(8)");
        DATATYPE_MAPPING.put(Integer.class, "NUMBER(8)");
        DATATYPE_MAPPING.put(Long.class, "NUMBER(10)");
        DATATYPE_MAPPING.put(Float.class, "NUMBER(8, 2)");
        DATATYPE_MAPPING.put(Double.class, "NUMBER(10, 2)");
        DATATYPE_MAPPING.put(BigDecimal.class, "NUMBER(20, 6)");
        DATATYPE_MAPPING.put(Timestamp.class, "DATE");
        DATATYPE_MAPPING.put(Date.class, "DATE");
        DATATYPE_MAPPING.put(Time.class, "DATE");
    }
    
    public OracleDialect() {
        super();
    }

    @Override
    public String data_type(Class<?> javatype) {
        String dbDataType = DATATYPE_MAPPING.get(javatype);
        if (dbDataType == null) {
            throw new IllegalArgumentException("No oracle data type found for java type: " + javatype.getSimpleName());
        }
        return dbDataType;
    }

    @Override
    public String db_url(String host, String port, String db) {
        return "jdbc:oracle:thin:@" + host + ":" + port + ":" + db;
    }

    @Override
    public String run_command(String sqlFile) {
        return "-- @" + sqlFile + " --";
    }

    @Override
    public String query_tables(String schema) {
        return "SELECT table_name FROM user_tables";
    }

    @Override
    protected String auto_gen_data_type() {
        return "GENERATED ALWAYS AS IDENTITY";
    }

    @Override
    public String timestamp() {
        return "SELECT SYSDATE FROM DUAL";
    }

    @Override
    public String version() {
        return "SELECT SYSDATE, banner FROM v$version";
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
