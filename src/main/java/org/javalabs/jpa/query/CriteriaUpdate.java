package org.javalabs.jpa.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author schan280
 */
public class CriteriaUpdate {
    
    private static final List<Visitor> VISITORS = Arrays.asList(
            new UpdateExprVisitor(),
            new SetExprVisitor(),
            new WhereExprVisitor());
    
    UpdateExpr updateExpr;
    
    // The SET clause.
    SetExpr setExpr;
    
    // Indicate whether the query has a WHERE clause.
    boolean whereStarted = false;
    WhereExpr whereExpr;
    
    // Temporarily hold the current condition.
    Condition cond;
    
    // Indicates whether the final query would have a BETWEEN clause.
    boolean between = false;
    
    private String col = null;
    private Object currVal = null;
    
    final QueryBuffer buff;
    final List<Object> params;
    
    public CriteriaUpdate() {
        buff = SBPoolImpl.getInstance().lookup();
        params = new ArrayList<>();
    }
    
    public CriteriaUpdate update(String table) {
        updateExpr = new UpdateExpr(buff, table);
        return this;
    }
    
    public CriteriaUpdate set(String col) {
        if (this.updateExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying SET without UPDATE clause");
        }
        /*if (this.whereExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying SET after WHERE clause");
        }*/
        this.col = col;
        this.setExpr = new SetExpr(buff);
        return this;
    }
    
    public CriteriaUpdate eq(Object val) {
        precheck(val);
        
        if (whereStarted) {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.EQ, val));
        }
        else {
            setExpr.add(cond, new QueryExpr(buff, col, Operator.EQ, val));
        }
        col = null;
        params.add(val);
        return this;
    }
    
    private void precheck(Object val) {
        if (col == null) {
            throw new IllegalArgumentException("Invalid criteria query format."
                    + " Passing value [" + val + "] without column");
        }
    }
    
    public CriteriaUpdate where(String col) {
        if (this.setExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying WHERE without SET clause");
        }
        this.whereStarted = true;
        this.col = col;
        this.whereExpr = new WhereExpr(buff);
        return this;
    }
    
    public CriteriaUpdate and(Object col) {
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
    
    public CriteriaUpdate or(String col) {
        if (whereExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying or without where clause");
        }
        this.col = col;
        this.cond = Condition.OR;
        return this;
    }
    
    public CriteriaUpdate isNull() {
        precheck("NULL");
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.ISNULL, null));
        col = null;
        return this;
    }
    
    public CriteriaUpdate isNotNull() {
        precheck("NOT_NULL");
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.ISNOTNULL, null));
        col = null;
        return this;
    }
    
    public CriteriaUpdate between(Object val) {
        precheck(val);
        this.currVal = val;
        this.between = true;
        
        return this;
    }
    
    public CriteriaUpdate like(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.LIKE, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public <T> CriteriaUpdate in(Collection<T> val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.IN, val));
        
        col = null;
        params.addAll(val);
        
        return this;
    }
    
    public CriteriaUpdate gt(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.GT, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public CriteriaUpdate lt(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.LT, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public CriteriaUpdate gte(Object val) {
        precheck(val);
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.GTE, val));
        
        col = null;
        params.add(val);
        
        return this;
    }
    
    public CriteriaUpdate lte(Object val) {
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
    
    String query(boolean release) {
        if (updateExpr != null) {
            updateExpr.accept(VISITORS.get(0));
        }
        if (setExpr != null) {
            setExpr.accept(VISITORS.get(1));
        }
        else {
            throw new IllegalArgumentException("UPDATE statement does not have any SET clause");
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
