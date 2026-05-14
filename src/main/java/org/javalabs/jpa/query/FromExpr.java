package org.javalabs.jpa.query;

/**
 * From clause expression
 *
 * @author Sudiptasish Chanda
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
