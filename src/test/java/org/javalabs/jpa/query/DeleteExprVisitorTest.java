package org.javalabs.jpa.query;

import org.javalabs.jpa.query.UpdateExpr;
import org.javalabs.jpa.query.DeleteExprVisitor;
import org.javalabs.jpa.query.SBPoolImpl;
import org.javalabs.jpa.query.QueryBuffer;
import org.junit.jupiter.api.Test;

public class DeleteExprVisitorTest {

    QueryBuffer buff = SBPoolImpl.getInstance().lookup();
    @Test
    public void testDeleteExpr() {
        DeleteExprVisitor delExpr = new DeleteExprVisitor();
        UpdateExpr expr = new UpdateExpr(buff ,"ecm_database");
        delExpr.visit(expr);
    }
}
