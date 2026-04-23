package org.javalabs.jpa.dialect.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 *
 * @author schan280
 */
@Entity
@Table(name = "information_schema.columns")
public class ColumnMetadata {

    @Column(name = "table_catalog")
    private String tableCatalog;

    @Column(name = "table_schema")
    private String tableSchema;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "ordinal_position")
    private Integer ordinalPosition;

    @Column(name = "column_default")
    private String columnDefault;

    @Column(name = "is_nullable")
    private String isNullable;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "character_maximum_length")
    private Integer characterMaximumLength;

    @Column(name = "character_octet_length")
    private Integer characterOctetLength;

    @Column(name = "numeric_precision")
    private Integer numericPrecision;

    @Column(name = "numeric_precision_radix")
    private Integer numericPrecisionRadix;

    @Column(name = "numeric_scale")
    private Integer numericScale;

    @Column(name = "datetime_precision")
    private Integer datetimePrecision;

    @Column(name = "interval_type")
    private String intervalType;

    @Column(name = "interval_precision")
    private Integer intervalPrecision;

    @Column(name = "character_set_catalog")
    private String characterSetCatalog;

    @Column(name = "character_set_schema")
    private String characterSetSchema;

    @Column(name = "character_set_name")
    private String characterSetName;

    @Column(name = "collation_catalog")
    private String collationCatalog;

    @Column(name = "collation_schema")
    private String collationSchema;

    @Column(name = "collation_name")
    private String collationName;

    @Column(name = "domain_catalog")
    private String domainCatalog;

    @Column(name = "domain_schema")
    private String domainSchema;

    @Column(name = "domain_name")
    private String domainName;

    @Column(name = "udt_catalog")
    private String udtCatalog;

    @Column(name = "udt_schema")
    private String udtSchema;

    @Column(name = "udt_name")
    private String udtName;

    @Column(name = "scope_catalog")
    private String scopeCatalog;

    @Column(name = "scope_schema")
    private String scopeSchema;

    @Column(name = "scope_name")
    private String scopeName;

    @Column(name = "maximum_cardinality")
    private Integer maximumCardinality;

    @Column(name = "dtd_identifier")
    private String dtdIdentifier;

    @Column(name = "is_self_referencing")
    private String isSelfReferencing;

    @Column(name = "is_identity")
    private String isIdentity;

    @Column(name = "identity_generation")
    private String identityGeneration;

    @Column(name = "identity_start")
    private String identityStart;

    @Column(name = "identity_increment")
    private String identityIncrement;

    @Column(name = "identity_maximum")
    private String identityMaximum;

    @Column(name = "identity_minimum")
    private String identityMinimum;

    @Column(name = "identity_cycle")
    private String identityCycle;

    @Column(name = "is_generated")
    private String isGenerated;

    @Column(name = "generation_expression")
    private String generationExpression;

    @Column(name = "is_updatable")
    private String isUpdatable;

    public String getTableCatalog() {
        return tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public void setCharacterMaximumLength(Integer characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    public Integer getCharacterOctetLength() {
        return characterOctetLength;
    }

    public void setCharacterOctetLength(Integer characterOctetLength) {
        this.characterOctetLength = characterOctetLength;
    }

    public Integer getNumericPrecision() {
        return numericPrecision;
    }

    public void setNumericPrecision(Integer numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    public Integer getNumericPrecisionRadix() {
        return numericPrecisionRadix;
    }

    public void setNumericPrecisionRadix(Integer numericPrecisionRadix) {
        this.numericPrecisionRadix = numericPrecisionRadix;
    }

    public Integer getNumericScale() {
        return numericScale;
    }

    public void setNumericScale(Integer numericScale) {
        this.numericScale = numericScale;
    }

    public Integer getDatetimePrecision() {
        return datetimePrecision;
    }

    public void setDatetimePrecision(Integer datetimePrecision) {
        this.datetimePrecision = datetimePrecision;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public Integer getIntervalPrecision() {
        return intervalPrecision;
    }

    public void setIntervalPrecision(Integer intervalPrecision) {
        this.intervalPrecision = intervalPrecision;
    }

    public String getCharacterSetCatalog() {
        return characterSetCatalog;
    }

    public void setCharacterSetCatalog(String characterSetCatalog) {
        this.characterSetCatalog = characterSetCatalog;
    }

    public String getCharacterSetSchema() {
        return characterSetSchema;
    }

    public void setCharacterSetSchema(String characterSetSchema) {
        this.characterSetSchema = characterSetSchema;
    }

    public String getCharacterSetName() {
        return characterSetName;
    }

    public void setCharacterSetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }

    public String getCollationCatalog() {
        return collationCatalog;
    }

    public void setCollationCatalog(String collationCatalog) {
        this.collationCatalog = collationCatalog;
    }

    public String getCollationSchema() {
        return collationSchema;
    }

    public void setCollationSchema(String collationSchema) {
        this.collationSchema = collationSchema;
    }

    public String getCollationName() {
        return collationName;
    }

    public void setCollationName(String collationName) {
        this.collationName = collationName;
    }

    public String getDomainCatalog() {
        return domainCatalog;
    }

    public void setDomainCatalog(String domainCatalog) {
        this.domainCatalog = domainCatalog;
    }

    public String getDomainSchema() {
        return domainSchema;
    }

    public void setDomainSchema(String domainSchema) {
        this.domainSchema = domainSchema;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getUdtCatalog() {
        return udtCatalog;
    }

    public void setUdtCatalog(String udtCatalog) {
        this.udtCatalog = udtCatalog;
    }

    public String getUdtSchema() {
        return udtSchema;
    }

    public void setUdtSchema(String udtSchema) {
        this.udtSchema = udtSchema;
    }

    public String getUdtName() {
        return udtName;
    }

    public void setUdtName(String udtName) {
        this.udtName = udtName;
    }

    public String getScopeCatalog() {
        return scopeCatalog;
    }

    public void setScopeCatalog(String scopeCatalog) {
        this.scopeCatalog = scopeCatalog;
    }

    public String getScopeSchema() {
        return scopeSchema;
    }

    public void setScopeSchema(String scopeSchema) {
        this.scopeSchema = scopeSchema;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public Integer getMaximumCardinality() {
        return maximumCardinality;
    }

    public void setMaximumCardinality(Integer maximumCardinality) {
        this.maximumCardinality = maximumCardinality;
    }

    public String getDtdIdentifier() {
        return dtdIdentifier;
    }

    public void setDtdIdentifier(String dtdIdentifier) {
        this.dtdIdentifier = dtdIdentifier;
    }

    public String getIsSelfReferencing() {
        return isSelfReferencing;
    }

    public void setIsSelfReferencing(String isSelfReferencing) {
        this.isSelfReferencing = isSelfReferencing;
    }

    public String getIsIdentity() {
        return isIdentity;
    }

    public void setIsIdentity(String isIdentity) {
        this.isIdentity = isIdentity;
    }

    public String getIdentityGeneration() {
        return identityGeneration;
    }

    public void setIdentityGeneration(String identityGeneration) {
        this.identityGeneration = identityGeneration;
    }

    public String getIdentityStart() {
        return identityStart;
    }

    public void setIdentityStart(String identityStart) {
        this.identityStart = identityStart;
    }

    public String getIdentityIncrement() {
        return identityIncrement;
    }

    public void setIdentityIncrement(String identityIncrement) {
        this.identityIncrement = identityIncrement;
    }

    public String getIdentityMaximum() {
        return identityMaximum;
    }

    public void setIdentityMaximum(String identityMaximum) {
        this.identityMaximum = identityMaximum;
    }

    public String getIdentityMinimum() {
        return identityMinimum;
    }

    public void setIdentityMinimum(String identityMinimum) {
        this.identityMinimum = identityMinimum;
    }

    public String getIdentityCycle() {
        return identityCycle;
    }

    public void setIdentityCycle(String identityCycle) {
        this.identityCycle = identityCycle;
    }

    public String getIsGenerated() {
        return isGenerated;
    }

    public void setIsGenerated(String isGenerated) {
        this.isGenerated = isGenerated;
    }

    public String getGenerationExpression() {
        return generationExpression;
    }

    public void setGenerationExpression(String generationExpression) {
        this.generationExpression = generationExpression;
    }

    public String getIsUpdatable() {
        return isUpdatable;
    }

    public void setIsUpdatable(String isUpdatable) {
        this.isUpdatable = isUpdatable;
    }
}
