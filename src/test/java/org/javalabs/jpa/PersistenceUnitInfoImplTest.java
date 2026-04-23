package org.javalabs.jpa;

import org.javalabs.jpa.PersistenceUnitInfoImpl;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import org.junit.jupiter.api.Test;

public class PersistenceUnitInfoImplTest {

    @Test
    public void testPersistence() {
        String version = "2.0";
        String name = "jpa-pu";
        String desc = "ECM Persistence Unit";
        String provider =  "org.javalabs.jpa.LitePersistenceProvider";
        PersistenceUnitInfoImpl persist = new PersistenceUnitInfoImpl(version,name,desc,
                provider,Enum.valueOf(PersistenceUnitTransactionType.class,PersistenceUnitTransactionType.RESOURCE_LOCAL.name()));
        persist.setSharedCacheMode(SharedCacheMode.NONE);
        persist.setExcludeUnlisted(true);
        persist.addMappingFile("mappingfile");

        persist.getDescription();
        persist.getPersistenceProviderClassName();
        persist.getJtaDataSource();
        persist.getNonJtaDataSource();
        persist.getMappingFileNames();
        persist.excludeUnlistedClasses();
        persist.getSharedCacheMode();
        persist.getValidationMode();
        persist.getPersistenceXMLSchemaVersion();
        persist.getClassLoader();


    }
}
