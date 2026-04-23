package org.javalabs.jpa.query;

import java.util.Collection;

/**
 * A concrete visitor class for evaluating {@link Expr}
 * 
 * <p>
 * As of today, {@link ExprVisitor} is responsible for evaluating all sorts of
 * expression. However, in future, multiple {@link Visitor} classes can be introduced
 * to handle operator specific logic.
 *
 * @author schan280
 */
public class ExprVisitor implements Visitor {

    @Override
    public void visit(Visitable v) {
        if (v instanceof Expr) {
            QueryExpr expr = (QueryExpr)v;
            
            if (expr.ops() == Operator.BETWEEN) {
                Object[] val = (Object[])expr.rhs();
                expr.buffer.append(expr.lhs())
                        .append(expr.ops().symbol())
                        .append("?")
                        .append(" AND ")
                        .append("?");
            }
            else if (expr.ops() == Operator.ISNULL || expr.ops() == Operator.ISNOTNULL) {
                expr.buffer.append(expr.lhs()).append(expr.ops().symbol());
            }
            else if (expr.ops() == Operator.IN || expr.ops() == Operator.NOT_IN) {
                expr.buffer.append(expr.lhs())
                        .append(expr.ops().symbol())
                        .append("(");
                        //.append(":")
                        //.append(expr.lhs())
                        //.append(")");
                
                int length = ((Collection)expr.rhs()).size();
                for (int i = 0; i < length; i ++) {
                    expr.buffer.append("?");
                    if (i < length - 1) {
                        expr.buffer.append(", ");
                    }
                }
                expr.buffer.append(")");
            }
            else {
                expr.buffer.append(expr.lhs())
                        .append(expr.ops().symbol())
                        .append("?");
            }
        }
    }
    
}
