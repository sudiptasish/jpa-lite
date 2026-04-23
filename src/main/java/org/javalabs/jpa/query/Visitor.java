package org.javalabs.jpa.query;

/**
 * Interface that represents a visitor.
 * 
 * <p>
 * In {@link Criteria} query, user may provide multiple criteria to fetch the 
 * required (matching) data from the underlying data store structured as one colossal
 * graph. Each node of the graph may represent an {@link Expr}, e.g., a criteria.
 * The nodes are connected with others by the standard join operators. While forming
 * the final SQL query, it is essential to visit all the nodes a.k.a. expression,
 * parse them and make them part of the final structure. Visitor design pattern helps
 * us achieving the same.
 *
 * @author schan280
 */
public interface Visitor {
    
    /**
     * API that should be called by the {@link Visitable} object upon accepting
     * the request to visit.
     * 
     * @param v     The {@link Visitable} object, that is being visited by this {@link Visitor}.
     */
    void visit(Visitable v);
}
