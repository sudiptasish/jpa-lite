package org.javalabs.jpa.query;

import org.junit.jupiter.api.Test;

public class DeleteExprVisitorTest {

    QueryBuffer buff = SBPoolImpl.getInstance().lookup();
    @Test
    public void testDeleteExpr() {
        DeleteExprVisitor delExpr = new DeleteExprVisitor();
        UpdateExpr expr = new UpdateExpr(buff ,"test_database");
        delExpr.visit(expr);
    }
}
