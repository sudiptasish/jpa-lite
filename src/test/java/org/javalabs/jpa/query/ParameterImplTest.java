package org.javalabs.jpa.query;

import org.javalabs.jpa.query.ParameterImpl;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ParameterImplTest {

    @Test
    public void  testParameterImpl(){
        String name = "name";
        Integer position = 2;
        Class<?> paramType = Date.class;
        ParameterImpl param = new ParameterImpl(name,position,paramType);

        param.getName();
    }
}
