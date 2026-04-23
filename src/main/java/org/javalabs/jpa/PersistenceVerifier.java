package org.javalabs.jpa;

import org.javalabs.jpa.dialect.Dialect;
import jakarta.persistence.spi.PersistenceProvider;
import jakarta.persistence.spi.PersistenceUnitInfo;
import java.util.Arrays;
import java.util.Properties;

/**
 * This verifier class verifies the database properties in the persistence config file.
 *
 * @author schan280
 */
public class PersistenceVerifier {
    
    private final PersistenceHolder holder = PersistenceHolder.getInstance();
    
    private final PersistenceProvider provider;
    
    private final String[] mandatoryParams = {"javax.persistence.jdbc.url"
            , "javax.persistence.jdbc.user"
            , "javax.persistence.jdbc.password"};
    
    PersistenceVerifier(PersistenceProvider provider) {
        this.provider = provider;
    }
    
    /**
     * Return the underlying provider instance this verifier is
     * associated with.
     * 
     * @return PersistenceProvider
     */
    public PersistenceProvider getProvider() {
        return provider;
    }
    
    public void verify(String puName) {
        PersistenceUnitInfo puInfo = holder.getCache().get(puName);
        
        if (puInfo != null) {
            Properties props = puInfo.getProperties();
            for (int i = 0; i < mandatoryParams.length; i ++) {
                if (! props.containsKey(mandatoryParams[i])) {
                    throw new RuntimeException("Attribute " + mandatoryParams[i] + " is missing in persistence.xml");
                }
            }
            
            // Verify the dialect.
            String dialect = props.getProperty("jpa-lite.dialect");
            if (dialect != null) {
                try {
                    Dialect d = Enum.valueOf(Dialect.class, dialect.toUpperCase());
                    
                    // Check if the url contains this dialect.
                    if (! props.getProperty("javax.persistence.jdbc.url").contains(dialect.toLowerCase())) {
                        throw new RuntimeException("SQL dialect and jdbc url does not match."
                                + " Check if the database parameter is correctly configured.");
                    }
                }
                catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid dialect name: " + dialect
                            + ". Supported dialects: " + Arrays.toString(Dialect.values()));
                }
            }
        }
    }
}
