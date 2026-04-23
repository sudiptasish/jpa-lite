package org.javalabs.jpa.query;

/**
 * Visitor for {@link WhereExpr}.
 *
 * @author schan280
 */
public class SetExprVisitor implements Visitor {
    
    private final ExprVisitor exprVisitor = new ExprVisitor();

    @Override
    public void visit(Visitable v) {
        if (v instanceof SetExpr) {
            SetExpr expr = (SetExpr)v;
            expr.buffer.append("\n    SET ");
            
            // Process the first expression.
            expr.list.get(0).accept(exprVisitor);
            
            // Now, start processing the remaining expressions (if any).
            // It is purposely done to ensure the right condition is prefixed.
            // Remember, the number of conditions will always be one less than the
            // original number of criteria.
            for (int i = 1; i < expr.list.size(); i ++) {
                Condition cond = expr.conds.get(i - 1);
                expr.buffer.append(cond.getDesc()).append(" ");
                
                Expr cExpr = expr.list.get(i);
                cExpr.accept(exprVisitor);
            }
        }
    }
    
}
