package org.javalabs.jpa;

import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.spi.PersistenceProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Model Processor class.
 * 
 * <p>
 * This class is responsible for reading the persistence configuration file from
 * the classpath and initialize the persistence unit(s). Application can register
 * their own customization entry and exit point, and the framework will invoke
 * the customization layer.
 *
 * @author Sudiptasish Chanda
 */
public class ModelProcessor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelProcessor.class);
    
    private static final String PERSISTENCE_XML = "persistence.xml";
    
    private final PersistenceHolder holder = PersistenceHolder.getInstance();
    
    private final PersistenceProvider provider;
    
    ModelProcessor(PersistenceProvider provider) {
        this.provider = provider;
    }
    
    /**
     * Return the underlying provider instance this metadata generator is
     * associated with.
     * 
     * @return PersistenceProvider
     */
    public PersistenceProvider getProvider() {
        return provider;
    }
    
    public void process(String puName) throws ParserConfigurationException, SAXException, IOException {
        // Read the persistence.xml file process the persistent unit.
        // Assumption: One persistence.xml file will have a single persistence-unit tag.
        long start = System.currentTimeMillis();
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsing jpa configuration file: {}", getFile());
        }
        Document doc = null;
        try (InputStream in = getInput()) {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            doc = domBuilder.parse(in);
        }
        Element root = doc.getDocumentElement();
        String version = root.getAttribute("version");
        
        NodeList list = root.getElementsByTagName("persistence-unit");
        for (byte i = 0; i < list.getLength(); i ++) {
            Node node = list.item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getAttribute("name");
                if (! name.equals(puName)) {
                    continue;
                }
                String description = element.getElementsByTagName("description").item(0).getTextContent();
                String provider = element.getElementsByTagName("provider").item(0).getTextContent();
                String txnType = element.getAttribute("transaction-type");
                if (txnType == null || txnType.trim().length() == 0) {
                    txnType = PersistenceUnitTransactionType.RESOURCE_LOCAL.name();
                }
                
                PersistenceUnitInfoImpl unitInfo = new PersistenceUnitInfoImpl(version
                    , name, description, provider, Enum.valueOf(PersistenceUnitTransactionType.class, txnType));
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found persistence unit: {}. Loading managed classes and properties", name);
                }
                
                // Now start loading the managed class.
                NodeList classes = element.getElementsByTagName("class");
                for (int j = 0; j < classes.getLength(); j ++) {
                    Node n = classes.item(j);
                    unitInfo.addManagedClass(n.getTextContent());
                }
                
                // Finally, add the properties.
                Node propNode = element.getElementsByTagName("properties").item(0);
                if (propNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element propElement = (Element)propNode;
                    
                    NodeList properties = propElement.getElementsByTagName("property");
                    for (byte j = 0; j < properties.getLength(); j ++) {
                        Node n = properties.item(j);
                        String k = ((Element)n).getAttribute("name");
                        String v = ((Element)n).getAttribute("value");
                        
                        unitInfo.addProperty(k, _sys(v));
                    }
                }
                holder.getCache().store(unitInfo);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Completed loading file: {}. Elapsed time(ms): {}"
                , getFile()
                , (System.currentTimeMillis() - start));
        }
        if (! holder.getCache().isLoaded(puName)) {
            throw new RuntimeException("Persistence unit name " + puName + " not found in " + getFile());
        }
    }
    
    private String getFile() {
        String file = System.getenv("ORM_CONFIG_FILE");
        if (file == null) {
            file = System.getProperty("orm.config.file", PERSISTENCE_XML);
        }
        return file;
    }
    
    private InputStream getInput() throws IOException {
        InputStream in = null;
        String filename = getFile();
        
        File file = new File(filename);
        if (file.exists()) {
            in = new FileInputStream(file);
        }
        else {
            in = getClass().getClassLoader().getResourceAsStream(filename);
            if (in != null) {

            }
            else {
                URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                if (url != null) {
                    String urlFile = url.getFile();
                    urlFile = urlFile.replaceAll("%20", " ");
                    file = new File(urlFile);
                    if (! file.exists()) {
                        // Read using loader
                        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
                    }
                    else {
                        in = new FileInputStream(new File(urlFile));
                    }
                }
            }
        }
        return in;
    }
    
    public String _sys(String value) {
        int start = value.indexOf("system(");
        if (start == -1) {
            start = value.indexOf("SYSTEM(");
        }
        if (start == 0) {
            int end = value.indexOf(")");
            if (end > 0) {
                String param = value.substring(7, end);
                String[] tmp = param.split("::");
                if (tmp.length == 2) {
                    String val = System.getProperty(tmp[0], tmp[1]);
                    return val;
                }
                else {
                    String val = System.getProperty(tmp[0]);
                    return val;
                }
            }
        }
        return value;
    }
    
    public void process(PersistenceConfiguration pc) {
        PersistenceUnitInfoImpl unitInfo = new PersistenceUnitInfoImpl("4.0", pc.name(), "", getClass().getName(), Enum.valueOf(PersistenceUnitTransactionType.class, PersistenceUnitTransactionType.RESOURCE_LOCAL.name()));

        // Now start loading the managed class.
        for (int j = 0; j < pc.managedClasses().size(); j ++) {
            Class<?> clazz = pc.managedClasses().get(j);
            unitInfo.addManagedClass(clazz.getName());
        }
        holder.getCache().store(unitInfo);
    }
}
