package org.javalabs.jpa.query;

/**
 * Visitor for {@link GroupByExpr}.
 *
 * @author schan280
 */
public class GroupByExprVisitor implements Visitor {

    private final ExprVisitor exprVisitor = new ExprVisitor();

    @Override
    public void visit(Visitable v) {
        if (v instanceof GroupByExpr) {
            GroupByExpr expr = (GroupByExpr)v;
            expr.buffer.append("\n  GROUP BY ");
            
            addColumns(expr);
            if (expr.having) {
                expr.buffer.append("\n HAVING ");
                
                // Process the first expression.
                expr.list.get(0).accept(exprVisitor);

                // Now, start processing the remaining expressions (if any).
                // It is purposely done to ensure the right condition is prefixed.
                // Remember, the number of conditions will always be one less than the
                // original number of criteria.
                for (int i = 1; i < expr.list.size(); i ++) {
                    Condition cond = expr.conds.get(i - 1);
                    expr.buffer.append("\n    ").append(cond.name()).append(" ");

                    Expr cExpr = expr.list.get(i);
                    cExpr.accept(exprVisitor);
                }
            }
        }
    }
    
    private void addColumns(GroupByExpr expr) {
        for (int i = 0; i < expr.columns.size(); i ++) {
            expr.buffer.append(expr.columns.get(i));
            if (i < expr.columns.size() - 1) {
                expr.buffer.append(", ");
            }
        }
    }
}
