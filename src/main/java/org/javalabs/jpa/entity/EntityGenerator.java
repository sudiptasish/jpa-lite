package org.javalabs.jpa.entity;

import org.javalabs.jpa.dialect.Dialect;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class EntityGenerator {
    
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
    
    private static void generate(String[] args) {
        try {
            String project = null;
            String pkg = null;
            String dialect = null;
            String host = null;
            String port = null;
            String db = null;
            String schema = null;
            String user = null;
            String password = null;
            String tname = null;
            String exPrefix = null;
            String verbose = "Y";
            
            for (int i = 0; i < args.length - 1; i ++) {
                if (args[i].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                    System.out.println(help());
                    return;
                }
                else if (args[i].equals("-j") || args[i].equals("--project-dir")) {
                    project = args[i + 1];
                }
                else if (args[i].equals("-k") || args[i].equals("--package")) {
                    pkg = args[i + 1];
                }
                else if (args[i].equals("-d") || args[i].equals("--dialect")) {
                    dialect = args[i + 1];
                }
                else if (args[i].equals("-h") || args[i].equals("--db-host")) {
                    host = args[i + 1];
                }
                else if (args[i].equals("-p") || args[i].equals("--db-port")) {
                    port = args[i + 1];
                }
                else if (args[i].equals("-n") || args[i].equals("--db-name")) {
                    db = args[i + 1];
                }
                else if (args[i].equals("-s") || args[i].equals("--db-schema")) {
                    schema = args[i + 1];
                }
                else if (args[i].equals("-u") || args[i].equals("--db-user")) {
                    user = args[i + 1];
                }
                else if (args[i].equals("-w") || args[i].equals("--db-password")) {
                    password = args[i + 1];
                }
                else if (args[i].equals("-t") || args[i].equals("--table-name")) {
                    tname = args[i + 1];
                }
                else if (args[i].equals("-x") || args[i].equals("--exclude-prefix")) {
                    exPrefix = args[i + 1];
                }
                else if (args[i].equals("-v") || args[i].equals("--verbose")) {
                    verbose = args[i + 1];
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
            if (host == null) {
                System.out.println("Missing mandatory param '--db-host'");
                System.out.println(help());
                return;
            }
            if (port == null) {
                System.out.println("Missing mandatory param '--db-port'");
                System.out.println(help());
                return;
            }
            if (db == null) {
                System.out.println("Missing mandatory param '--db-name'");
                System.out.println(help());
                return;
            }
            if (schema == null) {
                System.out.println("Missing mandatory param '--db-schema'");
                System.out.println(help());
                return;
            }
            if (user == null) {
                System.out.println("Missing mandatory param '--db-user'");
                System.out.println(help());
                return;
            }
            if (password == null) {
                System.out.println("Missing mandatory param '--db-password'");
                System.out.println(help());
                return;
            }
            if (pkg == null) {
                System.out.println("Missing mandatory param '--package'");
                System.out.println(help());
                return;
            }
            
            if (project == null) {
                throw new IllegalArgumentException("Must specify the project root directory");
            }
            File file = new File(project);
            if (! file.exists() || ! file.isDirectory()) {
                throw new IllegalArgumentException("Project directory " + project + " does not exist");
            }
            
            Map<String, Object> params = new HashMap<>();
            params.put("model.gen", "true");
            params.put("jpa-lite.dialect", dialect);
            params.put("jpa-lite.entity.package", pkg);
            params.put("javax.persistence.jdbc.host", host);
            params.put("javax.persistence.jdbc.port", port);
            params.put("javax.persistence.jdbc.db", db);
            params.put("javax.persistence.jdbc.schema", schema);
            params.put("javax.persistence.jdbc.user", user);
            params.put("javax.persistence.jdbc.password", password);
            
            params.put("sample.table.name", tname);
            params.put("exclude.entity.name.prefix", exPrefix);
            params.put("verbose.log", verbose);
            
            generate(project, params);
        }
        catch (Exception e) {
            System.err.println("Error generating model class. Msg: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void generate(String project, Map<String, Object> params) throws IOException, JAXBException {
        String verbose = (String)params.get("verbose.log");
        long start = System.currentTimeMillis();
        
        // Step 1: Extract the table definition in a pojo from the underlying db.
        String ormXml = extract(project, params);
        
        if ("Y".equalsIgnoreCase(verbose)) {
            System.out.println("Extracted metadata for table(s). Size(byte): " + (ormXml.length() * 2)
                    + " Elapsed time(ms): " + (System.currentTimeMillis() - start));
            
            System.out.println(ormXml);
        }
        start = System.currentTimeMillis();
        
        // Convert them to javax.persistence compliant jpa entities.
        convert(project, (String)params.get("jpa-lite.entity.package"), ormXml, verbose);
        
        if ("Y".equalsIgnoreCase(verbose)) {
            System.out.println("Model class generation for the project is complete !"
                    + " Elapsed time(ms): " + (System.currentTimeMillis() - start));
        }
    }
    
    public static String extract(String project, Map<String, Object> params) {
        final MetadataExtractor extractor = new MetadataExtractor();
        String ormXml = extractor.extract(project, params);
        
        return ormXml;
    }
    
    public static void convert(String project, String pkgName, String ormXml, String verbose) throws IOException {
        final LtModelInitializer modelInit = new LtModelInitializer();
        Map<String, String> classes = modelInit.generateModels(ormXml);

        for (Map.Entry<String, String> me : classes.entrySet()) {
            if ("Y".equalsIgnoreCase(verbose)) {
                System.out.println("*** " + me.getKey() + ".java" + " ***");
                System.out.println(me.getValue());
            }
            Files.write(
                    Paths.get(project, "src/main/java/" + pkgName.replace('.', File.separatorChar) + File.separator + me.getKey() + ".java")
                    , me.getValue().getBytes()
                    , StandardOpenOption.CREATE_NEW);
        }
    }
    
    /**
     * Return the standard error message.
     * @return String
     */
    private static String help() {
        StringBuilder buff = new StringBuilder(256);
        buff.append("\n").append(String.format("%-10s: %s", "Description", "JPA-LiTE supports generating jpa entities from the database tables"));
        buff.append("\n").append(String.format("%-10s: %s", "Usage", "entity-gen [OPTIONS] ..."));
        buff.append("\n").append(String.format("%-10s: %s", "Example", "entity-gen -d postgres -p <test-pu> -r <project.root.dir>"));
        buff.append("\n\n").append("The options are:");
        buff.append("\n\n");
        buff.append(String.format("%-40s %s\n", "-d [--dialect] <name>", "Database dialect [postgres, sybase, oracle, h2, db2, mysql, derby]"));
        buff.append(String.format("%-40s %s\n", "-j [--project-dir] <project.root.dir>", "Root directory of the projects where generated jpa entities will be placed"));
        buff.append(String.format("%-40s %s\n", "-k [--package] <package>", "Package name of the generated jpa entities"));
        buff.append(String.format("%-40s %s\n", "-h [--db-host] <host_name>", "Host name of the database server"));
        buff.append(String.format("%-40s %s\n", "-p [--db-port] <port>", "Port of the remote database server"));
        buff.append(String.format("%-40s %s\n", "-n [--db-name] <db_name>", "Remote database name"));
        buff.append(String.format("%-40s %s\n", "-s [--db-schema] <schema_name>", "Schema name of the database to be queried for table metadata"));
        buff.append(String.format("%-40s %s\n", "-u [--db-user] <db_user>", "Name of the database user"));
        buff.append(String.format("%-40s %s\n", "-w [--db-password] <db_password>", "Password of the database user"));
        buff.append(String.format("%-40s %s\n", "-t [--table-name] <sample_table>", "Pass the sample table name for testing purpose"));
        buff.append(String.format("%-40s %s\n", "-x [--exclude-prefix] <ex_prefix>", "Table name prefix that will be excluded while forming the final entity name."));
        buff.append(String.format("%-40s %s\n", "", "[E.g., for prefix 'ecm_', ecm_jobs will be mapped to Job class. In normal scenario, it would be EcmJob]"));
        buff.append(String.format("%-40s %s\n", "-v [--verbose]", "Verbose Output"));
        
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
}
