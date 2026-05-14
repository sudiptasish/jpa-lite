package org.javalabs.jpa.query;

/**
 * Represents an update expression in a query model.
 * 
 * <p>
 * Encapsulates field assignments and conditions used to construct
 * update operations programmatically.
 *
 * @author Sudiptasish Chanda
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
