package org.javalabs.jpa.dialect.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 *
 * @author schan280
 */
@Entity
@Table(name = "information_schema.check_constraints")
public class ConstraintMetadata {
    
    @Column(name = "table_name")
    private String tableName;
    
    @Column(name = "constraint_name")
    private String constraintName;
    
    @Column(name = "constraint_type")
    private String constraintType;
    
    @Column(name = "column_name")
    private String columnName;
    
    @Column(name = "check_clause")
    private String checkClause;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCheckClause() {
        return checkClause;
    }

    public void setCheckClause(String checkClause) {
        this.checkClause = checkClause;
    }
}
