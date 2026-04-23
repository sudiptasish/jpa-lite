package org.javalabs.jpa.query;

import java.util.List;

/**
 * Order by expression.
 *
 * @author schan280
 */
public class OrderByExpr extends AbstractExpr {
    
    // Holds the set of columns to be included in the order by clause.
    final List<String> columns;
    
    // Indicate whether to sort the filtered set of data.
    boolean asc = true;
    
    final QueryBuffer buffer;
    
    OrderByExpr(QueryBuffer buffer, List<String> columns) {
        this.buffer = buffer;
        this.columns = columns;
    }
    
    void desc() {
        asc = false;
    }
}
