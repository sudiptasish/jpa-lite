package org.javalabs.jpa;

import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * Platform provided persistence unit info.
 * 
 * <p>
 * During bootstrap, the {@link LitePersistenceProvider} class will load the jps
 * configuration file, <code>persistence.xml</code>. This file has all the metadata
 * needed to bootstrap the jpa-LiTE framework. The information pertaining to a
 * specific persistence unit extracted from this file, will be kept in this class.
 *
 * @author Sudiptasish Chanda
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
    
    private final String version;
    private final String name;
    private final String description;
    private final String provider;
    private final PersistenceUnitTransactionType txnType;
    
    private DataSource jtaDatasource;
    private SharedCacheMode sharedCacheMode = SharedCacheMode.NONE;
    private boolean excludeUnlisted = true;
    
    private final List<String> mappingFiles = new ArrayList<>();
    private final List<String> managedClasses = new ArrayList<>();
    private final Properties props = new Properties();

    public PersistenceUnitInfoImpl(String version
        , String name
        , String description
        , String provider
        , PersistenceUnitTransactionType txnType) {
        
        this.version = version;
        this.name = name;
        this.description = description;
        this.provider = provider;
        this.txnType = txnType;
    }

    public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.sharedCacheMode = sharedCacheMode;
    }

    public void setExcludeUnlisted(boolean excludeUnlisted) {
        this.excludeUnlisted = excludeUnlisted;
    }
    
    public void addMappingFile(String mappingFile) {
        mappingFiles.add(mappingFile);
    }
    
    public void addManagedClass(String entityClass) {
        managedClasses.add(entityClass);
    }
    
    public void addProperty(String key, String value) {
        props.setProperty(key, value);
    }

    @Override
    public String getPersistenceUnitName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return provider;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return txnType;
    }

    @Override
    public DataSource getJtaDataSource() {
        return jtaDatasource;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return null;
    }

    @Override
    public List<String> getMappingFileNames() {
        return mappingFiles;
    }

    @Override
    public List<URL> getJarFileUrls() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getManagedClassNames() {
        return managedClasses;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return excludeUnlisted;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return sharedCacheMode;
    }

    @Override
    public ValidationMode getValidationMode() {
        return ValidationMode.NONE;
    }

    @Override
    public Properties getProperties() {
        return props;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return version;
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getScopeAnnotationName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getQualifierAnnotationNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getAllManagedClassNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FetchType getDefaultToOneFetchType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
