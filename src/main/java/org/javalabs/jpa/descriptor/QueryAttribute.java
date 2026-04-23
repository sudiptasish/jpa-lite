package org.javalabs.jpa.descriptor;

/**
 * Attributes of a Named/Native Query.
 *
 * @author Sudiptasish Chanda
 */
public class QueryAttribute {

    private String name = "";
    private String query = "";
    private boolean nativeSql = false;

    public QueryAttribute() {
    }
    
    public QueryAttribute(String name, String query, boolean nativeSql) {
        this.name = name;
        this.query = query;
        this.nativeSql = nativeSql;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String query() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean nativeSql() {
        return nativeSql;
    }

    public void setNativeSql(boolean nativeSql) {
        this.nativeSql = nativeSql;
    }
}