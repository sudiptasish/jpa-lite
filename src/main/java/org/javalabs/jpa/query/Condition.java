package org.javalabs.jpa.query;

/**
 * Enum to hold the various conditions.
 *
 * @author schan280
 */
public enum Condition {
    
    AND ("AND"),
    OR ("OR"),
    COMMA (",");
    
    final String desc;
    
    Condition(String desc) {
        this.desc = desc;
    }
    
    public String getDesc() {
        return this.desc;
    }
}
