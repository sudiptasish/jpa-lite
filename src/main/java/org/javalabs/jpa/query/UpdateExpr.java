package org.javalabs.jpa.query;

/**
 *
 * @author schan280
 */
public class UpdateExpr extends AbstractExpr {
    
    // Underlying table to be updated.
    final String table;
    
    final QueryBuffer buffer;
    
    UpdateExpr(QueryBuffer buffer, String table) {
        this.buffer = buffer;
        this.table = table;
    }
}
