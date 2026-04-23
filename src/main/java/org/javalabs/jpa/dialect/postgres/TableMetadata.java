package org.javalabs.jpa.dialect.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author schan280
 */
@Entity
@Table(name = "information_schema.tables")
public class TableMetadata {

    @Column(name = "table_catalog")
    private String tableCatalog;

    @Column(name = "table_schema")
    private String tableSchema;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "table_type")
    private String tableType;

    @Column(name = "self_referencing_column_name")
    private String selfReferencingColumn_name;

    @Column(name = "reference_generation")
    private String referenceGeneration;

    @Column(name = "user_defined_type_catalog")
    private String userDefinedTypeCatalog;

    @Column(name = "user_defined_type_schema")
    private String userDefinedTypeSchema;

    @Column(name = "user_defined_type_name")
    private String userDefinedTypeName;

    @Column(name = "is_insertable_into")
    private String isInsertableInto;

    @Column(name = "is_typed")
    private String isTyped;

    @Column(name = "table_catalog")
    private String commit_action;
    
    @Transient
    private List<ColumnMetadata> columns = new ArrayList<>();
    
    @Transient
    private List<PrimaryKeyMetadata> pkColumns = new ArrayList<>();

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

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getSelfReferencingColumn_name() {
        return selfReferencingColumn_name;
    }

    public void setSelfReferencingColumn_name(String selfReferencingColumn_name) {
        this.selfReferencingColumn_name = selfReferencingColumn_name;
    }

    public String getReferenceGeneration() {
        return referenceGeneration;
    }

    public void setReferenceGeneration(String referenceGeneration) {
        this.referenceGeneration = referenceGeneration;
    }

    public String getUserDefinedTypeCatalog() {
        return userDefinedTypeCatalog;
    }

    public void setUserDefinedTypeCatalog(String userDefinedTypeCatalog) {
        this.userDefinedTypeCatalog = userDefinedTypeCatalog;
    }

    public String getUserDefinedTypeSchema() {
        return userDefinedTypeSchema;
    }

    public void setUserDefinedTypeSchema(String userDefinedTypeSchema) {
        this.userDefinedTypeSchema = userDefinedTypeSchema;
    }

    public String getUserDefinedTypeName() {
        return userDefinedTypeName;
    }

    public void setUserDefinedTypeName(String userDefinedTypeName) {
        this.userDefinedTypeName = userDefinedTypeName;
    }

    public String getIsInsertableInto() {
        return isInsertableInto;
    }

    public void setIsInsertableInto(String isInsertableInto) {
        this.isInsertableInto = isInsertableInto;
    }

    public String getIsTyped() {
        return isTyped;
    }

    public void setIsTyped(String isTyped) {
        this.isTyped = isTyped;
    }

    public String getCommit_action() {
        return commit_action;
    }

    public void setCommit_action(String commit_action) {
        this.commit_action = commit_action;
    }

    @Transient
    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    @Transient
    public void setColumns(List<ColumnMetadata> columns) {
        this.columns = columns;
    }

    @Transient
    public List<PrimaryKeyMetadata> getPkColumns() {
        return pkColumns;
    }

    @Transient
    public void setPkColumns(List<PrimaryKeyMetadata> pkColumns) {
        this.pkColumns = pkColumns;
    }
}
