package org.javalabs.jpa.query;

/**
 * Base abstraction for expressions used in query construction.
 * 
 * <p>
 * Provides common behavior and structure for different types of expressions
 * such as predicates, updates, and deletes.
 * <p>
 * Intended to be extended by concrete expression implementations.
 *
 * @author Sudiptasish Chanda
 */
public class AbstractExpr implements Visitable {

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
    
}
