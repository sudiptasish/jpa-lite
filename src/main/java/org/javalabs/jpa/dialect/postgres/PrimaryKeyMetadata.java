package org.javalabs.jpa.dialect.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 *
 * @author schan280
 */
@Entity
@Table(name = "information_schema.key_column_usage")
public class PrimaryKeyMetadata {

    @Column(name = "constraint_catalog")
    private String constraintCatalog;

    @Column(name = "constraint_schema")
    private String constraintSchema;

    @Column(name = "constraint_name")
    private String constraintName;

    @Column(name = "table_catalog")
    private String tableCatalog;

    @Column(name = "table_schema")
    private String tableSchema;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "ordinal_position")
    private String ordinalPosition;

    @Column(name = "position_in_unique_constraint")
    private String positionInUniqueConstraint;

    public String getConstraintCatalog() {
        return constraintCatalog;
    }

    public void setConstraintCatalog(String constraintCatalog) {
        this.constraintCatalog = constraintCatalog;
    }

    public String getConstraintSchema() {
        return constraintSchema;
    }

    public void setConstraintSchema(String constraintSchema) {
        this.constraintSchema = constraintSchema;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

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

    public String getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(String ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getPositionInUniqueConstraint() {
        return positionInUniqueConstraint;
    }

    public void setPositionInUniqueConstraint(String positionInUniqueConstraint) {
        this.positionInUniqueConstraint = positionInUniqueConstraint;
    }
}
