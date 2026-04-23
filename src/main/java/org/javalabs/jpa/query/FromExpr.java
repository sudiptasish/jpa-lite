package org.javalabs.jpa.query;

/**
 * From clause expression
 *
 * @author schan280
 */
public class FromExpr extends AbstractExpr {
    
    // The table name
    final String from;
    
    final QueryBuffer buffer;
    
    FromExpr(QueryBuffer buffer, String from) {
        this.buffer = buffer;
        this.from = from;
    }
}
