package org.javalabs.jpa.dialect;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.dialect.postgres.ColumnMetadata;
import org.javalabs.jpa.dialect.postgres.PGJaxbOrmBridge;
import org.javalabs.jpa.dialect.postgres.PrimaryKeyMetadata;
import org.javalabs.jpa.dialect.postgres.TableMetadata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Index;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class PostgresDialect extends AbstractDBDialect {

    private static final Map<Class<?>, String> DATATYPE_MAPPING = new HashMap<>();

    static {
        DATATYPE_MAPPING.put(byte[].class, "BYTEA");
        DATATYPE_MAPPING.put(String.class, "VARCHAR");
        DATATYPE_MAPPING.put(Boolean.class, "SMALLINT");
        DATATYPE_MAPPING.put(Byte.class, "SMALLINT");
        DATATYPE_MAPPING.put(Short.class, "INT");
        DATATYPE_MAPPING.put(Integer.class, "INT");
        DATATYPE_MAPPING.put(Long.class, "BIGINT");
        DATATYPE_MAPPING.put(Float.class, "NUMERIC(10, 2)");
        DATATYPE_MAPPING.put(Double.class, "NUMERIC(10, 2)");
        DATATYPE_MAPPING.put(BigDecimal.class, "NUMERIC(20, 6)");
        DATATYPE_MAPPING.put(Timestamp.class, "TIMESTAMP");
        DATATYPE_MAPPING.put(Date.class, "DATE");
        DATATYPE_MAPPING.put(Time.class, "TIME");
    }

    public PostgresDialect() {
        super();
    }

    @Override
    public String data_type(Class<?> javatype) {
        String dbDataType = DATATYPE_MAPPING.get(javatype);
        if (dbDataType == null) {
            throw new IllegalArgumentException("No postgres data type found for java type: " + javatype.getSimpleName());
        }
        return dbDataType;
    }

    @Override
    public String db_url(String host, String port, String db) {
        return "jdbc:postgresql://" + host + ":" + port + "/" + db;
    }

    @Override
    public String run_command(String sqlFile) {
        return "-- psql -d ecmdb -U ecm -a -f " + sqlFile + " --";
    }

    @Override
    public String query_tables(String schema) {
        return "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
    }

    @Override
    public String index(ClassDescriptor desc) {
        Index[] indexes = desc.indexes();
        if (indexes == null || indexes.length == 0) {
            return "";
        }
        StringBuilder script = new StringBuilder(32);

        for (int i = 0; i < indexes.length; i++) {
            script.append("CREATE ").append(indexes[i].unique() ? "UNIQUE" : "").append(" INDEX ")
                    .append(indexes[i].name() != null
                            && indexes[i].name().length() > 0
                            ? indexes[i].name()
                            : desc.table() + (indexes[i].unique() ? "_uk" : "_ik"))
                    .append(indexes[i].name() != null
                            && indexes[i].name().length() > 0
                            ? "" : (indexes.length > 1 ? i : ""))
                    .append("\n").append("ON ").append(desc.table())
                    .append("\n").append("USING BTREE ").append("(").append(indexes[i].columnList()).append(")")
                    .append(";");
        }
        return script.toString();
    }

    @Override
    protected String auto_gen_data_type() {
        return "SERIAL";
    }

    @Override
    public String timestamp() {
        return "SELECT CURRENT_TIMESTAMP";
    }

    @Override
    public String version() {
        return "SELECT CURRENT_TIMESTAMP, version()";
    }

    @Override
    public String db_table_metadata(EntityManager em, Map<String, Object> props) {
        String dbName = (String)props.get("javax.persistence.jdbc.db");
        String schema = (String)props.get("javax.persistence.jdbc.schema");
        String tableName = (String)props.get("sample.table.name");
        
        TypedQuery query = em.createNativeQuery(
                "\n  SELECT *"
                + "\n  FROM information_schema.tables"
                + "\n WHERE table_catalog = ?"
                + "\n   AND table_schema = ?"
                + "\n   AND table_type = ?"
                + "\n   AND table_name LIKE ?", TableMetadata.class);
        
        query.setParameter(1, dbName);
        query.setParameter(2, schema);
        query.setParameter(3, "BASE TABLE");
        query.setParameter(4, tableName != null ? ("%" + tableName + "%") : "%%");
        
        List<TableMetadata> tables = query.getResultList();
        
        for (TableMetadata tableMD : tables) {
            query = em.createNativeQuery(
                    "\n  SELECT *"
                    + "\n  FROM information_schema.columns"
                    + "\n WHERE table_catalog = ?"
                    + "\n   AND table_schema = ?"
                    + "\n   AND table_name = ?"
                    + "\n ORDER BY ordinal_position", ColumnMetadata.class);
            
            query.setParameter(1, dbName);
            query.setParameter(2, schema);
            query.setParameter(3, tableMD.getTableName());
            
            List<ColumnMetadata> columns = query.getResultList();
            tableMD.setColumns(columns);
            
            query = em.createNativeQuery(
                    "\n  SELECT b.*"
                    + "\n  FROM information_schema.table_constraints a"
                    + "\n INNER JOIN information_schema.key_column_usage b ON ("
                    + "\n            a.constraint_catalog = b.constraint_catalog"
                    + "\n        AND a.constraint_schema = b.constraint_schema"
                    + "\n        AND a.constraint_name = b.constraint_name"
                    + "\n        AND a.table_catalog = b.table_catalog"
                    + "\n        AND a.table_schema = b.table_schema"
                    + "\n        AND a.table_name = b.table_name"
                    + "\n        AND a.table_catalog = ?"
                    + "\n        AND a.table_schema = ?"
                    + "\n        AND a.table_name = ?"
                    + "\n        AND a.constraint_type = ?)"
                    + "\n ORDER BY b.ordinal_position", PrimaryKeyMetadata.class);
            
            query.setParameter(1, dbName);
            query.setParameter(2, schema);
            query.setParameter(3, tableMD.getTableName());
            query.setParameter(4, "PRIMARY KEY");
            
            List<PrimaryKeyMetadata> pkColumns = query.getResultList();
            tableMD.setPkColumns(pkColumns);
        }
        // Convert to orm entity types.
        return new PGJaxbOrmBridge().covertToXml(tables, props);
    }

    @Override
    public List<Class<?>> metadataClasses() {
        return Arrays.asList(TableMetadata.class, ColumnMetadata.class, PrimaryKeyMetadata.class);
    }
}
