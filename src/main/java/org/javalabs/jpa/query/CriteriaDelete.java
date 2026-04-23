package org.javalabs.jpa.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author schan280
 */
public class CriteriaDelete {
    
    private static final List<Visitor> VISITORS = Arrays.asList(
            new DeleteExprVisitor(),
            new FromExprVisitor(),
            new WhereExprVisitor());
    
    DeleteExpr deleteExpr;
    
    // The underlying table name (FROM clause).
    FromExpr fromExpr;
    
    // Indicate whether the query has a WHERE clause.
    WhereExpr whereExpr;
    
    // Temporarily hold the current condition.
    Condition cond;
    
    // Indicates whether the final query would have a BETWEEN clause.
    boolean between = false;
    
    private String col = null;
    private Object currVal = null;
    
    final QueryBuffer buff;
    final List<Object> params;
    
    public CriteriaDelete() {
        buff = SBPoolImpl.getInstance().lookup();
        params = new ArrayList<>();
    }
    
    public CriteriaDelete delete() {
        deleteExpr = new DeleteExpr(buff);
        return this;
    }
    
    public CriteriaDelete from(String from) {
        if (this.deleteExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying FROM without DELETE clause");
        }
        this.fromExpr = new FromExpr(buff, from);
        return this;
    }
    
    public CriteriaDelete where(String col) {
        if (this.fromExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying WHERE without FROM clause");
        }
        this.col = col;
        this.whereExpr = new WhereExpr(buff);
        return this;
    }
    
    public CriteriaDelete eq(Object val) {
        precheck(val);
        
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.EQ, val));
        col = null;
        params.add(val);
        return this;
    }
    
    public CriteriaDelete and(Object col) {
        if (between) {
            // this.col = column name
            // col = value
            whereExpr.add(cond, new QueryExpr(buff, this.col, Operator.BETWEEN, new Object[] {this.currVal, col}));
            
            this.col = null;
            params.add(currVal);
            params.add(col);        // Here the col is treated as column value.
            this.between = false;
        }
        else {
            if (whereExpr == null) {
                throw new IllegalArgumentException("Invalid query format."
                        + " Specifying and without where clause");
            }
            this.col = (String)col;
            this.cond = Condition.AND;
        }
        return this;
    }
    
    public CriteriaDelete or(String col) {
        if (whereExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying or without where clause");
        }
        this.col = col;
        this.cond = Condition.OR;
        return this;
    }
    
    public CriteriaDelete isNull() {
        precheck("NULL");
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.ISNULL, null));
        col = null;
        return this;
    }
    
    public CriteriaDelete isNotNull() {
        precheck("NOT_NULL");
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.ISNOTNULL, null));
        col = null;
        return this;
    }
    
    public CriteriaDelete between(Object val) {
        precheck(val);
        this.currVal = val;
        this.between = true;
        
        return this;
    }
    
    public CriteriaDelete like(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.LIKE, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public <T> CriteriaDelete in(Collection<T> val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.IN, val));
        
        col = null;
        params.addAll(val);
        
        return this;
    }
    
    public CriteriaDelete gt(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.GT, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public CriteriaDelete lt(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.LT, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public CriteriaDelete gte(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.GTE, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public CriteriaDelete lte(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.LTE, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public String toQuery() {
        return query(true);
    }
    
    public List<Object> params() {
        return params;
    }
    
    private void precheck(Object val) {
        if (col == null) {
            throw new IllegalArgumentException("Invalid criteria query format."
                    + " Passing value [" + val + "] without column");
        }
    }
    
    String query(boolean release) {
        if (deleteExpr != null) {
            deleteExpr.accept(VISITORS.get(0));
        }
        if (fromExpr != null) {
            fromExpr.accept(VISITORS.get(1));
        }
        if (whereExpr != null) {
            whereExpr.accept(VISITORS.get(2));
        }
        String raw = buff.toString();
        if (release) {
            SBPoolImpl.getInstance().release(buff);
        }
        return raw;
    }
}
