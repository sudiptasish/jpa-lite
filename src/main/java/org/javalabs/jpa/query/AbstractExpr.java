package org.javalabs.jpa.query;

/**
 *
 * @author schan280
 */
public class AbstractExpr implements Visitable {

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
    
}
