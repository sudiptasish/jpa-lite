package org.javalabs.jpa.query;

/**
 * Interface that represents a visitable object.
 *
 * <p>
 * Whatever activities the {@link Visitable} object has to perform, it will do so
 * by passing its reference to the {@link Visitor#visit(org.javalabs.jpa.query.Visitable) }
 * method as an argument, providing the method access to all necessary data contained
 * within this object.
 * 
 * @author schan280
 */
public interface Visitable {
    
    /**
     * Method that will be called to allow the {@link Visitor} to pay a visit.
     * @param v     Visitor object.
     */
    void accept(Visitor v);
}
