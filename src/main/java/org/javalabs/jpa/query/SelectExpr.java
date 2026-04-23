package org.javalabs.jpa.query;

import java.util.List;

/**
 * Select expression.
 *
 * @author schan280
 */
public class SelectExpr extends AbstractExpr {
    
    // Indicates whether the final query would be a DISTINCT query.
    final boolean distinct;
    
    // Indicates whether the generated query would a simple COUNT query.
    final boolean count;
    
    // Holds the set of columns to be selected from the underlying table.
    final List<String> columns;
    
    final QueryBuffer buffer;
    
    SelectExpr(QueryBuffer buffer, List<String> columns) {
        this(buffer, columns, false, false);
    }
    
    SelectExpr(QueryBuffer buffer, List<String> columns, boolean distinct, boolean count) {
        this.buffer = buffer;
        this.columns = columns;
        this.distinct = distinct;
        this.count = count;
    }
}
