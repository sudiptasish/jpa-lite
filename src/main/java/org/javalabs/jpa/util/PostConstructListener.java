package org.javalabs.jpa.util;

import java.util.Properties;

/**
 * The listener that will be called after the persistence unit is loaded.
 * 
 * <p>
 * Application component can choose to provide their own custom listener. The jpa-LiTE
 * framework will ensure to invoke the {@link #callback(javax.persistence.spi.PersistenceUnitInfo) }
 * method after initializing the framework. Application can choose to override
 * any properties, especially the database connection details, etc.
 *
 * @author Sudiptasish Chanda
 */
public interface PostConstructListener {
    
    /**
     * Callback method to be invoked by the lite framework.
     * @param props Properties that user can override.
     */
    void callback(Properties props);
}
