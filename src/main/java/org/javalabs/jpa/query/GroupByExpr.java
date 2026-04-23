package org.javalabs.jpa.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Group by expression.
 *
 * @author schan280
 */
public class GroupByExpr extends AbstractExpr {
    
    // Holds the set of columns to be included in the order by clause.
    final List<String> columns;
    
    boolean having = false;
    
    final List<Condition> conds = new ArrayList<>(1);
    
    // List that contains the criteria expressions.
    final List<Expr> list = new ArrayList<>(2);
    
    final QueryBuffer buffer;
    
    GroupByExpr(QueryBuffer buffer, List<String> columns) {
        this.buffer = buffer;
        this.columns = columns;
    }
    
    void having() {
        this.having = true;
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
