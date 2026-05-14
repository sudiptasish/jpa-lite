package org.javalabs.jpa.query;

/**
 * Represents a delete expression in a query model.
 * 
 * <p>
 * Encapsulates the structure and conditions required to perform delete
 * operations on a target entity or table.
 *
 * @author Sudiptasish Chanda
 */
public class DeleteExpr extends AbstractExpr {
    
    final QueryBuffer buffer;
    
    DeleteExpr(QueryBuffer buffer) {
        this.buffer = buffer;
    }
}
