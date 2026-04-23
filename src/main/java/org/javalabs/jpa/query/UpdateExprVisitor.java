package org.javalabs.jpa.query;

/**
 * Visitor for {@link SelectExpr}.
 *
 * @author schan280
 */
public class UpdateExprVisitor implements Visitor {

    @Override
    public void visit(Visitable v) {
        if (v instanceof UpdateExpr) {
            UpdateExpr expr = (UpdateExpr)v;
            expr.buffer.append("\n UPDATE ").append(expr.table);
        }
    }
}
