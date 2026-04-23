package org.javalabs.jpa.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * JPA Criteria query.
 * 
 * <p>
 * A query criterion is an expression that jpa compares to query field values
 * to determine whether to include the record that contains each value.
 * For example, <code>location = 'NY'</code> is an expression that jpa can compare
 * to values in a text field in a pooledBuffer. 
 * Another example could be, <code>dept IN ('HR', 'R&D')</code>.
 * 
 * Criteria queries are written using Java programming language APIs, are typesafe,
 * and are portable. Such queries work regardless of the underlying data store.
 * 
 * Example 1: Simple select query.
 * 
 * <pre>
 * {@code 
 * 
 * Criteria criteria = new Criteria()
 *     .select("emp_id", "emp_name", "location")
 *     .from("employees");
 * }
 * </pre>
 * <br>
 * 
 * Example 2: Select query with conditions.
 * 
 * <pre>
 * {@code 
 * 
 * Criteria criteria = new Criteria()
 *     .select("emp_id", "emp_name", "location")
 *     .from("employees")
 *     .where("emp_mame").like("%John%")
 *     .and("location").in(Arrays.asList("NY", "LN"));
 * }
 * </pre>
 * <br>
 * 
 * Example 3: Select query with order by.
 * 
 * <pre>
 * {@code 
 * 
 * Criteria criteria = new Criteria()
 *     .select("emp_id", "emp_name", "location")
 *     .from("employees")
 *     .where("emp_mame").like("%John%")
 *     .orderBy("location")
 *     .desc();
 * }
 * </pre>
 * <br>
 * 
 * Example 4: Select query with group by.
 * 
 * <pre>
 * {@code 
 * 
 * Criteria criteria = new Criteria()
 *     .select("department", "AVG(salary)")
 *     .from("employees")
 *     .where("location").eq("NY")
 *     .groupBy("department")
 *     .having("AVG(salary)").gt(20000)
 *     .orderBy("AVG(salary)")
 *     .desc();
 * }
 * </pre>
 *
 * @author schan280
 */
public class Criteria {
    
    private static final List<Visitor> VISITORS = Arrays.asList(
            new SelectExprVisitor(),
            new FromExprVisitor(),
            new WhereExprVisitor(),
            new GroupByExprVisitor(),
            new OrderByExprVisitor());
    
    Criteria parent;
    
    Criteria with;
    
    String withAlias;
    
    SelectExpr selectExpr;
    
    // The underlying table name (FROM clause).
    FromExpr fromExpr;
    
    // Indicate whether the query has a WHERE clause.
    WhereExpr whereExpr;
    
    // Temporarily hold the current condition.
    Condition cond;
    
    // Indicates whether the final query would have a BETWEEN clause.
    boolean between = false;
    
    // Holds the set of columns used in GROUP BY clause.
    GroupByExpr groupBy = null;
    
    // Holds the set of columns used in ORDER BY clause.
    OrderByExpr orderBy = null;
    
    private String col = null;
    private Object currVal = null;
    
    final QueryBuffer buff;
    final List<Object> params;
    
    public Criteria() {
        this(null);
    }
    
    public Criteria(Criteria parent) {
        this.parent = parent;
        this.params = new ArrayList<>();
        
        if (parent != null) {
            buff = parent.buff;
        }
        else {
            buff = SBPoolImpl.getInstance().lookup();
        }
    }
    
    public Criteria with(String table) {
        withAlias = table;
        return this;
    }
    
    public Criteria as() {
        with = new Criteria(this);
        return with;
    }
    
    public Criteria doneWith() {
        return this.parent;
    }
    
    public Criteria select(List<String> columns) {
        return select(columns, false, false);
    }
    
    public Criteria select(String... columns) {
        return select(Arrays.asList(columns), false, false);
    }
    
    public Criteria selectDistinct(List<String> columns) {
        return select(columns, true, false);
    }
    
    public Criteria selectDistinct(String... columns) {
        return select(Arrays.asList(columns), true, false);
    }
    
    public Criteria selectCount(String expr) {
        return select(Arrays.asList(expr), false, true);
    }
    
    public Criteria selectCountDistinct(List<String> columns) {
        return select(columns, true, true);
    }
    
    public Criteria selectCountDistinct(String... columns) {
        return select(Arrays.asList(columns), true, true);
    }
    
    public Criteria select(List<String> columns, boolean distinct, boolean count) {
        this.selectExpr = new SelectExpr(buff, columns, distinct, count);
        return this;
    }
    
    public Criteria from(String from) {
        if (this.selectExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying from without select clause");
        }
        this.fromExpr = new FromExpr(buff, from);
        return this;
    }
    
    public Criteria where(String col) {
        if (this.fromExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying where without from clause");
        }
        this.col = col;
        this.whereExpr = new WhereExpr(buff);
        return this;
    }
    
    public Criteria and(Object col) {
        if (between) {
            // this.col = column name
            // col = value
            if (groupBy != null && groupBy.having) {
                groupBy.add(cond, new QueryExpr(buff, this.col, Operator.BETWEEN, new Object[] {this.currVal, col}));
            }
            else {
                whereExpr.add(cond, new QueryExpr(buff, this.col, Operator.BETWEEN, new Object[] {this.currVal, col}));
            }
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
    
    public Criteria or(String col) {
        if (whereExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying or without where clause");
        }
        this.col = col;
        this.cond = Condition.OR;
        return this;
    }
    
    public Criteria isNull() {
        precheck("NULL");
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.ISNULL, null));
        col = null;
        return this;
    }
    
    public Criteria isNotNull() {
        precheck("NOT_NULL");
        whereExpr.add(cond, new QueryExpr(buff, col, Operator.ISNOTNULL, null));
        col = null;
        return this;
    }
    
    public Criteria between(Object val) {
        precheck(val);
        this.currVal = val;
        this.between = true;
        
        return this;
    }
    
    public Criteria like(Object val) {
        precheck(val);
        if (groupBy != null && groupBy.having) {
            groupBy.add(cond, new QueryExpr(buff, col, Operator.LIKE, val));
        }
        else {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.LIKE, val));
        }
        col = null;
        params.add(val);
        
        return this;
    }
    
    public <T> Criteria in(Collection<T> val) {
        precheck(val);
        if (groupBy != null && groupBy.having) {
            groupBy.add(cond, new QueryExpr(buff, col, Operator.IN, val));
        }
        else {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.IN, val));
        }
        col = null;
        params.addAll(val);
        
        return this;
    }
    
    public Criteria eq(Object val) {
        precheck(val);
        if (groupBy != null && groupBy.having) {
            groupBy.add(cond, new QueryExpr(buff, col, Operator.EQ, val));
        }
        else {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.EQ, val));
        }
        col = null;
        params.add(val);
        
        return this;
    }
    
    public Criteria gt(Object val) {
        precheck(val);
        if (groupBy != null && groupBy.having) {
            groupBy.add(cond, new QueryExpr(buff, col, Operator.GT, val));
        }
        else {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.GT, val));
        }
        col = null;
        params.add(val);
        
        return this;
    }
    
    public Criteria lt(Object val) {
        precheck(val);
        if (groupBy != null && groupBy.having) {
            groupBy.add(cond, new QueryExpr(buff, col, Operator.LT, val));
        }
        else {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.LT, val));
        }
        col = null;
        params.add(val);
        
        return this;
    }
    
    public Criteria gte(Object val) {
        precheck(val);
        if (groupBy != null && groupBy.having) {
            groupBy.add(cond, new QueryExpr(buff, col, Operator.GTE, val));
        }
        else {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.GTE, val));
        }
        col = null;
        params.add(val);
        
        return this;
    }
    
    public Criteria lte(Object val) {
        precheck(val);
        if (groupBy != null && groupBy.having) {
            groupBy.add(cond, new QueryExpr(buff, col, Operator.LTE, val));
        }
        else {
            whereExpr.add(cond, new QueryExpr(buff, col, Operator.LTE, val));
        }
        col = null;
        params.add(val);
        
        return this;
    }
    
    public Criteria orderBy(String... columns) {
        return orderBy(Arrays.asList(columns));
    }
    
    public Criteria orderBy(List<String> columns) {
        if (this.fromExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying order by without from clause");
        }
        this.orderBy = new OrderByExpr(buff, columns);
        return this;
    }
    
    public Criteria desc() {
        if (this.orderBy == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying asc/desc without order by clause");
        }
        this.orderBy.desc();
        return this;
    }
    
    public Criteria groupBy(String... columns) {
        return groupBy(Arrays.asList(columns));
    }
    
    public Criteria groupBy(List<String> columns) {
        if (this.fromExpr == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying group by without from clause");
        }
        this.groupBy = new GroupByExpr(buff, columns);
        return this;
    }
    
    public Criteria having(String col) {
        if (this.groupBy == null) {
            throw new IllegalArgumentException("Invalid query format."
                    + " Specifying having without group by clause");
        }
        this.groupBy.having();
        this.col = col;
        return this;
    }
    
    public String toQuery() {
        return query(true);
    }
    
    public List<Object> params() {
        return params;
    }
    
    String query(boolean release) {
        if (withAlias != null) {
            buff.append("\nWITH ").append(withAlias).append(" AS (");
            with.query(false);
            buff.append("\n)");
        }
        if (selectExpr != null) {
            selectExpr.accept(VISITORS.get(0));
        }
        if (fromExpr != null) {
            fromExpr.accept(VISITORS.get(1));
        }
        if (whereExpr != null) {
            whereExpr.accept(VISITORS.get(2));
        }
        if (groupBy != null) {
            groupBy.accept(VISITORS.get(3));
        }
        if (orderBy != null) {
            orderBy.accept(VISITORS.get(4));
        }
        String raw = buff.toString();
        if (release) {
            SBPoolImpl.getInstance().release(buff);
        }
        return raw;
    }
    
    private void precheck(Object val) {
        if (col == null) {
            throw new IllegalArgumentException("Invalid criteria query format."
                    + " Passing value [" + val + "] without column");
        }
    }
}
