package org.javalabs.jpa.query;

/**
 * Represents a criteria expression.
 * 
 * <p>
 * An {@link Expr} object represents a primitive expression in which a single criteria
 * is applied to a target and a set of arguments to return a result. An {@link Expr}
 * has three components:
 * <ul>
 *   <li>L.H.S - Left operand, which is mostly the name of the table column</li>
 *   <li>Operator</li>
 *   <li>R.H.S - Right operand, which is the value for the column</li>
 * </ul>
 * 
 * Followings are the example of some valid expressions:
 * <ul>
 *   <li>location = 'NY'</li>
 *   <li>salary &gt; 35000</li>
 *   <li>age &lt;= 50</li>
 *   <li>age BETWEEN 18 AND 40</li>
 *   <li>department IN ('HR', 'RAndD', 'Finance')</li>
 * </ul>
 * 
 * @author Sudiptasish Chanda
 */
public interface Expr extends Visitable {
    
    /**
     * Return the left side operand of this expression.
     * @return String
     */
    String lhs();
    
    /**
     * Return the operator.
     * @return Operator
     */
    Operator ops();
    
    /**
     * Return the right side operand of this expression.
     * It will be null for ISNULL or ISNOTNULL operator.
     * For BETWEEN operator, it will have two values.
     * 
     * @return String
     */
    Object rhs();
}
