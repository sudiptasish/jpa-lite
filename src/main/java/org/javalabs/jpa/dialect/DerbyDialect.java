package org.javalabs.jpa.dialect;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Index;
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
public class DerbyDialect extends AbstractDBDialect {
    
    private static final Map<Class<?>, String> DATATYPE_MAPPING = new HashMap<>();
    
    static {
        DATATYPE_MAPPING.put(byte[].class, "BLOB");
        DATATYPE_MAPPING.put(String.class, "VARCHAR");
        DATATYPE_MAPPING.put(Boolean.class, "SMALLINT");
        DATATYPE_MAPPING.put(Byte.class, "SMALLINT");
        DATATYPE_MAPPING.put(Short.class, "INTEGER");
        DATATYPE_MAPPING.put(Integer.class, "INTEGER");
        DATATYPE_MAPPING.put(Long.class, "BIGINT");
        DATATYPE_MAPPING.put(Float.class, "NUMERIC(10, 2)");
        DATATYPE_MAPPING.put(Double.class, "NUMERIC(10, 2)");
        DATATYPE_MAPPING.put(BigDecimal.class, "NUMERIC(20, 6)");
        DATATYPE_MAPPING.put(Timestamp.class, "TIMESTAMP");
        DATATYPE_MAPPING.put(Date.class, "DATE");
        DATATYPE_MAPPING.put(Time.class, "TIME");
    }
    
    public DerbyDialect() {
        super();
    }

    @Override
    public String data_type(Class<?> javatype) {
        String dbDataType = DATATYPE_MAPPING.get(javatype);
        if (dbDataType == null) {
            throw new IllegalArgumentException("No derby data type found for java type: " + javatype.getSimpleName());
        }
        return dbDataType;
    }

    @Override
    public String run_command(String sqlFile) {
        return "-- run '" + sqlFile + "' --";
    }

    @Override
    public String db_url(String host, String port, String db) {
        return "jdbc:derby://" + host + ":" + port + "/" + db;
    }

    @Override
    public String query_tables(String schema) {
        return    "\nSELECT a.tablename"
                + "\n  FROM sys.systables a"
                + "\n INNER JOIN sys.sysschemas b ON (a.schemaid = b.schemaid AND b.schemaname = '" + schema.toUpperCase() + "')";
    }

    @Override
    public String index(ClassDescriptor desc) {
        Index[] indexes = desc.indexes();
        if (indexes == null || indexes.length == 0) {
            return "";
        }
        StringBuilder script = new StringBuilder(32);
        
        for (int i = 0; i < indexes.length; i ++) {
            script.append("CREATE ").append(indexes[i].unique() ? "UNIQUE " : "").append("INDEX ")
                    .append(indexes[i].name() != null
                            && indexes[i].name().length() > 0
                            ? indexes[i].name()
                            : desc.table() + (indexes[i].unique() ? "_uk" : "_ik"))
                    .append(indexes[i].name() != null
                            && indexes[i].name().length() > 0
                            ? "" : (indexes.length > 1 ? i : ""))
                    .append("\n").append("ON ").append(desc.table()).append(" (").append(indexes[i].columnList()).append(")")
                    .append(";");
            
            if (i < indexes.length - 1) {
                script.append("\n\n");
            }
        }
        return script.toString();
    }

    @Override
    protected String auto_gen_data_type() {
        return "INTEGER GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1)";
    }

    @Override
    public String timestamp() {
        return "SELECT CURRENT_TIMESTAMP";
    }

    @Override
    public String version() {
        return "SELECT CURRENT_TIMESTAMP, '1.0'";
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
