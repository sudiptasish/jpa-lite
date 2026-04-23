package org.javalabs.jpa.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public final class JpaEntity {
    
    private String pkg = "";
    private List<String> imports = new ArrayList<>();
    private String comment;
    private String author;
    private String table;
    private String className;
    private List<JpaField> fields = new ArrayList<>();
    private List<JpaField> pkfields = new ArrayList<>();
    
    private Map<String, String> nativeQueries = new HashMap<>();
    
    public JpaEntity() {}

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    
    public void addField(JpaField field) {
        fields.add(field);
    }

    public List<JpaField> getFields() {
        return fields;
    }

    public void setFields(List<JpaField> fields) {
        this.fields = fields;
    }

    public Map<String, String> getNativeQueries() {
        return nativeQueries;
    }

    public void setNativeQueries(Map<String, String> nativeQueries) {
        this.nativeQueries = nativeQueries;
    }

    public List<JpaField> getPkfields() {
        return pkfields;
    }

    public void setPkfields(List<JpaField> pkfields) {
        this.pkfields = pkfields;
    }
}
