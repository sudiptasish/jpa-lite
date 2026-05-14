package org.javalabs.jpa.entity;

/**
 * Represents metadata for a JPA entity field.
 * 
 * <p>
 * Encapsulates details such as field name, type, column mapping, and
 * persistence-related annotations.
 *
 * @author Sudiptasish Chanda
 */
public class JpaField {
    
    private Boolean id = Boolean.FALSE;
    private String strategy;
    private String name;
    private Class<?> type;
    private Integer length = 0;
    private Boolean nullable = Boolean.FALSE;
    private Boolean updatable = Boolean.TRUE;
    private Integer precision = 0;
    private Integer scale = 0;
    
    public JpaField() {}

    public Boolean getId() {
        return id;
    }

    public void setId(Boolean id) {
        this.id = id;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getUpdatable() {
        return updatable;
    }

    public void setUpdatable(Boolean updatable) {
        this.updatable = updatable;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return name;
    }
}
