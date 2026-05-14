package org.javalabs.jpa.query;

/**
 * Visitor for {@link FromExpr}.
 *
 * @author Sudiptasish Chanda
 */
public class FromExprVisitor implements Visitor {

    @Override
    public void visit(Visitable v) {
        if (v instanceof FromExpr) {
            FromExpr expr = (FromExpr)v;
            expr.buffer.append("\n   FROM ").append(expr.from);
        }
    }
    
}
