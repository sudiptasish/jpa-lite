package org.javalabs.jpa.query;

import org.javalabs.jpa.query.GroupByExpr;
import org.javalabs.jpa.query.Condition;
import org.javalabs.jpa.query.Expr;
import org.javalabs.jpa.query.QueryBuffer;
import org.javalabs.jpa.query.SetExpr;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class SetExprTest {

    @Test
    public void testSetExpr(){
        SetExpr setExpr = new SetExpr(new QueryBuffer(12));
        Condition cond = Condition.OR;
        Expr expr = null;
        setExpr.add(cond,expr);
    }

    @Test
    public void testGroupByExpr(){
        GroupByExpr groupByExpr = new GroupByExpr(new QueryBuffer(12),new ArrayList<>());
        Condition cond = Condition.OR;
        Expr expr = null;
        groupByExpr.add(cond,expr);
    }
}
