package org.javalabs.jpa.query;

/**
 * Visitor for {@link SelectExpr}.
 *
 * @author schan280
 */
public class SelectExprVisitor implements Visitor {

    @Override
    public void visit(Visitable v) {
        if (v instanceof SelectExpr) {
            SelectExpr expr = (SelectExpr)v;
            expr.buffer.append("\n SELECT ");
            
            if (expr.count) {
                expr.buffer.append("COUNT").append(" (");
                
                if (expr.distinct) {
                    expr.buffer.append("DISTINCT ");
                }
                addColumns(expr);
                expr.buffer.append(")");
            }
            else if (expr.distinct) {
                expr.buffer.append("DISTINCT ");
                addColumns(expr);
            }
            else {
                addColumns(expr);
            }
        }
    }
    
    private void addColumns(SelectExpr expr) {
        for (int i = 0; i < expr.columns.size(); i ++) {
            expr.buffer.append(expr.columns.get(i));
            if (i < expr.columns.size() - 1) {
                expr.buffer.append(", ");
            }
        }
    }
}
