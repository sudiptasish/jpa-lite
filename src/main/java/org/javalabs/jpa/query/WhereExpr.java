package org.javalabs.jpa.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Where clause expression.
 *
 * @author schan280
 */
public class WhereExpr extends AbstractExpr {
    
    final List<Condition> conds = new ArrayList<>(9);
    
    // List that contains the criteria expressions.
    final List<Expr> list = new ArrayList<>(10);
    
    final QueryBuffer buffer;
    
    WhereExpr(QueryBuffer buffer) {
        this.buffer = buffer;
    }
    
    /**
     * Add the new criteria expression along with the condition.
     * @param cond
     * @param expr 
     */
    void add(Condition cond, Expr expr) {
        if (cond != null) {
            // Very first iteration, the condition will be empty.
            // Think about having a single criteria only.
            this.conds.add(cond);
        }
        this.list.add(expr);
    }
}
