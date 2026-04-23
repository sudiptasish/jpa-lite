package org.javalabs.jpa.query;

/**
 *
 * @author schan280
 */
public class DeleteExpr extends AbstractExpr {
    
    final QueryBuffer buffer;
    
    DeleteExpr(QueryBuffer buffer) {
        this.buffer = buffer;
    }
}
