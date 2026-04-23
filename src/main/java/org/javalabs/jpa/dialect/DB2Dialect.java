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
public class DB2Dialect extends AbstractDBDialect {
    
    private static final Map<Class<?>, String> DATATYPE_MAPPING = new HashMap<>();
    
    static {
        DATATYPE_MAPPING.put(byte[].class, "BLOB");
        DATATYPE_MAPPING.put(String.class, "VARCHAR");
        DATATYPE_MAPPING.put(Boolean.class, "SMALLINT");
        DATATYPE_MAPPING.put(Byte.class, "SMALLINT");
        DATATYPE_MAPPING.put(Short.class, "INT");
        DATATYPE_MAPPING.put(Integer.class, "INT");
        DATATYPE_MAPPING.put(Long.class, "BIGINT");
        DATATYPE_MAPPING.put(Float.class, "NUMERIC(10, 2)");
        DATATYPE_MAPPING.put(Double.class, "NUMERIC(10, 2)");
        DATATYPE_MAPPING.put(BigDecimal.class, "DECIMAL(20, 6)");
        DATATYPE_MAPPING.put(Timestamp.class, "TIMESTAMP");
        DATATYPE_MAPPING.put(Date.class, "DATE");
        DATATYPE_MAPPING.put(Time.class, "TIME");
    }
    
    public DB2Dialect() {
        super();
    }

    @Override
    public String data_type(Class<?> javatype) {
        String dbDataType = DATATYPE_MAPPING.get(javatype);
        if (dbDataType == null) {
            throw new IllegalArgumentException("No db2 data type found for java type: " + javatype.getSimpleName());
        }
        return dbDataType;
    }

    @Override
    public String db_url(String host, String port, String db) {
        return "jdbc:db2://" + host + ":" + port + "/" + db;
    }

    @Override
    public String run_command(String sqlFile) {
        return "-- run 'path/to/script' --";
    }

    @Override
    public String query_tables(String schema) {
        return "SELECT tabname FROM syscat.tables WHERE tabschema = '" + schema.toUpperCase() + "' AND type = 'T'";
    }

    @Override
    protected String auto_gen_data_type() {
        return "GENERATED ALWAYS AS IDENTITY";
    }

    @Override
    public String timestamp() {
        return "SELECT CURRENT_TIMESTAMP FROM SYSIBM.SYSDUMMY1";
    }

    @Override
    public String version() {
        return "SELECT CURRENT_TIMESTAMP, GETVARIABLE('SYSIBM.VERSION') FROM SYSIBM.SYSDUMMY1";
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
