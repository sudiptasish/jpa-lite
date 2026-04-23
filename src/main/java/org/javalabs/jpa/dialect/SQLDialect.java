package org.javalabs.jpa.dialect;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;

/**
 * The SQL Dialect.
 * 
 * <p>
 * The constantly evolving nature of the SQL standard has given rise to a number of
 * SQL dialects among the various vendors and platforms. These dialects commonly
 * evolve because a given database vendor’s user community requires capabilities
 * in the database before the ANSI committee creates an applicable standard. Because
 * ANSI had not yet developed a standard for these important features at the time 
 * users began to demand them, RDBMS developers and vendors created their own commands
 * and syntax. In fact, some of the earliest vendors from the 1980s have variances in
 * the most elementary commands, such as SELECT, because their implementations predate
 * the standards. ANSI is now refining standards that address these inconsistencies.
 * 
 * <p>
 * The jpa-lite {@link SQLDialect} aims at providing consistencies across different RDBMSs,
 * when it comes to the following operation:
 * <ul>
 *   <li>Create Table</li>
 *   <li>Drop Table</li>
 *   <li>Create Constraint</li>
 *   <li>Drop Constraint</li>
 *   <li>Create Index</li>
 *   <li>Drop Index</li>
 * </ul>
 *
 * @author schan280
 */
public interface SQLDialect {
    
    /**
     * Form and return the jdbc database url.
     * 
     * @param host
     * @param port
     * @param db
     * @return String
     */
    String db_url(String host, String port, String db);
    
    /**
     * Return the database specific run command.
     * @param sqlFile
     * @return String
     */
    String run_command(String sqlFile);
    
    /**
     * Return the sql query to fetch the table names under a specific schema.
     * @param schema
     * @return String
     */
    String query_tables(String schema);
    
    /**
     * Return the database specific CREATE TABLE syntax.
     * @param desc
     * @return String
     */
    String create_table(ClassDescriptor desc);
    
    /**
     * Return the database specific DROP TABLE syntax.
     * @param desc
     * @return String
     */
    String drop_table(ClassDescriptor desc);
    
    /**
     * Return the database specific ALTER TABLE ADD CONSTRAINT syntax.
     * @param desc
     * @return String
     */
    String primary_key(ClassDescriptor desc);
    
    /**
     * Return the database specific CREATE {UNIQUE} INDEX syntax.
     * @param desc
     * @return String
     */
    String index(ClassDescriptor desc);
    
    /**
     * Return the database specific foreign key statement.
     * @param entity
     * @return String
     */
    String foreign_key(ClassDescriptor desc);
    
    /**
     * Return the rdbms specific data type for a given java data type.
     * 
     * <p>
     * Different RDBMS vendors have their own way of defining the data types.
     * E.g., a string column in Oracle is defined as CHAR or VARCHAR2, whereas the
     * same in Postgres is defined as VARCHAR. Therefore, depending on the current
     * dialect, this API will return the appropriate data type.
     * 
     * @param javatype  The java data type.
     * @return String   The corresponding rdbms data type.
     */
    String data_type(Class<?> javatype);
    
    /**
     * Return the timestamp query for this database.
     * @return String
     */
    String timestamp();
    
    /**
     * Return the version query for this database.
     * @return String
     */
    String version();
    
    /**
     * Return the classes needed to query the db and table metadata.
     * These classes are internal to vendor, and are jpa entities.
     * 
     * @return List
     */
    List<Class<?>> metadataClasses();
    
    /**
     * Query the database to get the metadata for table and associated columns and constraints.
     * It will generate the result in the form of a orm.xml.
     * 
     * @param em
     * @param props
     * 
     * @return String
     */
    String db_table_metadata(EntityManager em, Map<String, Object> props);
}
