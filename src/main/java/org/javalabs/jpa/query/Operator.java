package org.javalabs.jpa.query;

/**
 * Enum listing the set of supported operators for criteria query.
 * 
 * <p>
 * The operators mentioned here are the representation of the corresponding
 * SQL operator.
 *
 * @author schan280
 */
public enum Operator {
    
    EQ (" = "),
    LT (" < "),
    GT (" > "),
    LTE(" <= "),
    GTE(" >= "),
    BETWEEN (" BETWEEN "),
    IN (" IN "),
    NOT_IN (" NOT IN "),
    LIKE (" LIKE "),
    NOT_LIKE (" NOT LIKE "),
    ISNULL (" IS NULL "),
    ISNOTNULL (" IS NOT NULL ");
    
    private final String symbol;
    
    Operator(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Return the associated sql operator symbol.
     * @return String
     */
    public String symbol() {
        return symbol;
    }
}
