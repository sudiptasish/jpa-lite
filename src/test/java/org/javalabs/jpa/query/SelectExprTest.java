package org.javalabs.jpa.query;

import org.javalabs.jpa.query.SelectExpr;
import org.javalabs.jpa.query.QueryBuffer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class SelectExprTest {

    @Test
    public void testSelectExpr(){
        SelectExpr selectExpr = new SelectExpr(new QueryBuffer(12),new ArrayList<>());
    }
}
