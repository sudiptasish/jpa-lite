package org.javalabs.jpa.query;

/**
 * The expression criteria class to hold the parsed expression.
 *
 * @author schan280
 */
public class QueryExpr implements Expr {
    
    private final String lhs;
    private final Operator op;
    private final Object rhs;
    
    final QueryBuffer buffer;
    
    QueryExpr(QueryBuffer buffer, String lhs, Operator op, Object rhs) {
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
        this.buffer = buffer;
    }

    @Override
    public String lhs() {
        return lhs;
    }

    @Override
    public Operator ops() {
        return op;
    }

    @Override
    public Object rhs() {
        return rhs;
    }
    
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
    
}
