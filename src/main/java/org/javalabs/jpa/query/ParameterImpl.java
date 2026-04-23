package org.javalabs.jpa.query;

import jakarta.persistence.Parameter;

/**
 * Parameter class.
 *
 * @author Sudiptasish Chanda
 */
public class ParameterImpl implements Parameter {
    
    private final String name;
    private final Integer position;
    private final Class<?> paramType;

    public ParameterImpl(String name, Integer position, Class<?> paramType) {
        this.name = name;
        this.position = position;
        this.paramType = paramType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    @Override
    public Class<?> getParameterType() {
        return paramType;
    }
}
