package org.javalabs.jpa.query;

/**
 * Visitor for {@link OrderByExpr}.
 *
 * @author schan280
 */
public class OrderByExprVisitor implements Visitor {

    @Override
    public void visit(Visitable v) {
        if (v instanceof OrderByExpr) {
            OrderByExpr expr = (OrderByExpr)v;
            expr.buffer.append("\n  ORDER BY ");
            
            addColumns(expr);
            if (! expr.asc) {
                expr.buffer.append(" DESC");
            }
        }
    }
    
    private void addColumns(OrderByExpr expr) {
        for (int i = 0; i < expr.columns.size(); i ++) {
            expr.buffer.append(expr.columns.get(i));
            if (i < expr.columns.size() - 1) {
                expr.buffer.append(", ");
            }
        }
    }
}
