package org.javalabs.jpa.query;

/**
 * Visitor for {@link SelectExpr}.
 *
 * @author schan280
 */
public class DeleteExprVisitor implements Visitor {

    @Override
    public void visit(Visitable v) {
        if (v instanceof UpdateExpr) {
            UpdateExpr expr = (UpdateExpr)v;
            expr.buffer.append("\n DELETE ");
        }
    }
}
