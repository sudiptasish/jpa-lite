package org.javalabs.jpa.meta;

import org.javalabs.jpa.PersistenceHolder;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.spi.PersistenceProvider;
import jakarta.persistence.spi.PersistenceUnitInfo;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for generating the metadata for all managed classes.
 * 
 * <p>
 * Once a persistence unit is loaded into memory, this generator class is called,
 * which in turn iterates through all the managed classes and generates the
 * meta model for the same. This is an important step. The correct metamodel helps
 * maintaining the persistence api contract. If the metamodel generation fails, 
 * then the application bootstrap will fail.
 *
 * @author Sudiptasish Chanda
 */
public class MetadataGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataGenerator.class);
    
    private final PersistenceHolder holder = PersistenceHolder.getInstance();
    
    private final PersistenceProvider provider;

    public MetadataGenerator(PersistenceProvider provider) {
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
    
    /**
     * API to generate the metamodel for a given persistence unit.
     * @param puName
     */
    public void generate(String puName) {
        PersistenceUnitInfo puInfo = holder.getCache().get(puName);
        List<String> managedClasses = puInfo.getManagedClassNames();
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Metamodel will be generated for {} managed classes", managedClasses.size());
        }
        MetadataModelImpl metamodel = holder.getModel();
        
        MDExtractor extractor = MDExtractor.newExtractor();
        for (String managedClass : managedClasses) {
            EntityType<?> entityType = extractor.extract(managedClass);
            metamodel.addEntityType(entityType);
        }
    }
}
