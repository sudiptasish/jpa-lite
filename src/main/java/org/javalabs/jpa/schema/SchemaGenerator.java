package org.javalabs.jpa.schema;

import org.javalabs.jpa.dialect.Dialect;
import jakarta.persistence.PersistenceConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Schema generator.
 * 
 * <p>
 * JPA 2.1 standardized the configuration parameters to create the database schema
 * but using them is a little bit tricky. There is now a huge set of different parameters
 * and some of them depend on each other to be effective.
 * 
 * <p>
 * You need the the <code>persistence.xml</code> file in the classpath. A sample
 * structure with required properties are shown below.
 * 
 * <pre>
 * {@code 
 * 
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <persistence version="2.1">
 *     <persistence-unit name="ecm-pu">
 *         <provider>org.javalabs.jpa.LitePersistenceProvider</provider>
 *         <properties>
 *             <property name="jpa-lite.dialect" value="org.javalabs.dialect.PostgresDialect"/>
 *             <property name="jpa-lite.entity.package" value="org.javalabs.common.model"/>
 *             ....
 *             ....
 *             <property name="javax.persistence.schema-generation.scripts.action" value="create"/>
 *             <property name="javax.persistence.schema-generation.scripts.create-target" value="ecm_schema.sql"/>
 *             <property name="javax.persistence.sql-load-script-source" value="ecm_user.sql"/>
 *             <property name="javax.persistence.schema-generation.create-database-schemas" value="true"/>
 *         </properties>
 *     </persistence-unit>
 * </persistence>
 * }
 * </pre>
 * 
 * <p>
 * Once you start the schema generation, it will read the entities from the specified
 * package as identified by <code>jpa-lite.entity.package</code> attribute, and dump the
 * generated table script along with index definition in the file mentioned by
 * <code>javax.persistence.schema-generation.scripts.create-target</code> attribute.
 *
 * @author schan280
 */
public class SchemaGenerator {
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0 || (args.length == 1 && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")))) {
            System.err.println(help());
            return;
        }
        if (System.getProperties().containsKey("pause")) {
            Thread.sleep(5000);
        }
        disabledLogging();
        
        // Dump the table script.
        generate(args);
    }

    /**
     * Disable logging.
     */
    private static void disabledLogging() {
        // TODO
    }
    
    /**
     * Generate the table script and index definition for ecm schema.
     * 
     * <p>
     * This API will internally read the entity definition from <code>persistence.xml</code>
     * file and generate the table script for ecm schema.
     * @param args
     */
    public static void generate(String[] args) {
        try {
            String dialect = null;
            String sqlFile = null;
            String puName = "jpa-pu";
            String jars = null;
            
            for (int i = 0; i < args.length - 1; i ++) {
                if (args[i].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                    System.out.println(help());
                    return;
                }
                else if (args[i].equals("-d") || args[i].equals("--dialect")) {
                    dialect = args[i + 1];
                }
                else if (args[i].equals("-l") || args[i].equals("--entity-lib")) {
                    jars = args[i + 1];
                }
                else if (args[i].equals("-p") || args[i].equals("--pu-name")) {
                    puName = args[i + 1];
                }
                else if (args[i].equals("-o") || args[i].equals("--out-file")) {
                    sqlFile = args[i + 1];
                }
            }
            if (dialect != null) {
                try {
                    Dialect d = Enum.valueOf(Dialect.class, dialect.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid dialect name: " + dialect
                            + ". Valid values are: " + Arrays.toString(dialects()));
                }
            }
            Map<String, Object> params = new HashMap<>();
            params.put("schema.gen", "true");
            if (dialect != null) {
                params.put("jpa-lite.dialect", dialect);
            }
            else {
                System.out.println("No dialect is specified. Schema will be generated for H2 (default) database");
            }
            if (puName == null) {
                throw new IllegalArgumentException("Must rovide --pu-name (-p)");
            }
            if (sqlFile == null) {
                sqlFile = puName + ".sql";
            }
            params.put("javax.persistence.schema-generation.scripts.create-target", sqlFile);
            
            generate(jars, puName, params);
            System.out.println("\nDB script generation for schema is complete !");
        }
        catch (Exception e) {
            System.err.println("Error generating db schema. Msg: " + e.getMessage());
        }
    }
    
    public static void generate(String jars, String puName, Map<String, Object> params) {
        try {
            if (jars == null) {
                throw new IllegalArgumentException("Must specify the library/jar files having the jpa entities and \"persistence.xml\" file");
            }
            String[] files = jars.split(":");
            URL[] urls = new URL[files.length];

            for (int i = 0; i < files.length; i ++) {
                File jarFile = new File(files[i]);
                if (! jarFile.exists()) {
                    throw new IllegalArgumentException("Model jar file " + files[i] + " does not exist");
                }
                urls[i] = jarFile.toURI().toURL();
            }
            URLClassLoader loader = new URLClassLoader(urls, SchemaGenerator.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);

            Class<?> clazz = loader.loadClass("jakarta.persistence.Persistence");
            Method method = clazz.getDeclaredMethod("generateSchema", new Class[] {String.class, Map.class});
            method.invoke(null, new Object[] {puName, params});
        }
        catch (ClassNotFoundException | MalformedURLException | NoSuchMethodException
                | SecurityException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void generate(PersistenceConfiguration config) {
        try {
            Class<?> clazz = Class.forName("jakarta.persistence.Persistence");
            Method method = clazz.getDeclaredMethod("generateSchema", new Class[] {PersistenceConfiguration.class});
            method.invoke(null, new Object[] {config});
        }
        catch (ClassNotFoundException | NoSuchMethodException
                | SecurityException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Return the standard error message.
     * @return String
     */
    private static String help() {
        StringBuilder buff = new StringBuilder(256);
        buff.append("\n").append(String.format("%-10s: %s", "Description", "JPA-LiTE supports generating data definition langugage (DDL) or interact directly with the database to define table schemas based on the JPA entity definition"));
        buff.append("\n").append(String.format("%-10s: %s", "Usage", "schema-gen [OPTIONS] ..."));
        buff.append("\n").append(String.format("%-10s: %s", "Example", "schema-gen -d postgres -p <test-pu> -l jar1:jar2 -o test_schema.sql"));
        buff.append("\n\n").append("The options are:");
        buff.append("\n\n");
        buff.append(String.format("%-30s %s\n", "-d [--dialect] <name?", "Database dialect [postgres, sybase, oracle, h2, db2, mysql, derby]"));
        buff.append(String.format("%-30s %s\n", "-p [--pu-name] <pu_name>", "Name of the persistent unit, that should match with the one specified in the \"persistence.xml\""));
        buff.append(String.format("%-30s %s\n", "-l [--entity-lib] <jar_file>", "Jar(s) or location of the jpa entities along with \"persistence.xml\" used in your project"));
        buff.append(String.format("%-30s %s\n", "-o [--out-file] <test.sql>", "Output sql file. E.g., test_schema.sql"));
        buff.append(String.format("%-30s %s\n", "-v [--verbose]", "Verbose Output"));
        
        return buff.toString();
    }
    
    /**
     * Return the set of dialects currently supported by ecm.
     * @return String[]
     */
    private static String[] dialects() {
        Dialect[] dialects = Dialect.values();
        String[] dVals = new String[dialects.length];
        
        for (int i = 0; i < dialects.length; i ++) {
            if (Dialect.H2 == dialects[i]) {
                dVals[i] = dialects[i].name().toLowerCase() + " (default)";
            }
            else {
                dVals[i] = dialects[i].name().toLowerCase();
            }
        }
        return dVals;
    }
    
    /**
     * Read the secret filename from the classpath or from the specified directory.
     * 
     * @param filename      Secret file name (properties format)
     * @return InputStream  An input stream.
     * @throws IOException 
     */
    public static InputStream stream(final String filename) throws IOException {
        // File input stream for the file to be read
        InputStream in = null;

        File file = new File(filename);
        if (file.exists()) {
            in = new FileInputStream(file);
        }
        else {
            in = SchemaGenerator.class.getClassLoader().getResourceAsStream(filename);
            if (in != null) {

            }
            else {
                URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                if (url != null) {
                    String urlFile = url.getFile();
                    urlFile = urlFile.replaceAll("%20", " ");
                    in = new FileInputStream(new File(urlFile));
                }
            }
        }
        return in;
    }
}
