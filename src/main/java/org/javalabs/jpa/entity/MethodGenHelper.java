package org.javalabs.jpa.entity;

import org.javalabs.jpa.util.CharUtil;

/**
 *
 * @author schan280
 */
public final class MethodGenHelper {
    
    private static final String NEW_LINE      = "\n";
    private static final String TAB           = "    ";
    private static final String SPACE         = " ";
    private static final String SEMICOLON     = ";";
    
    void generateMethodDef(StringBuilder buff, JpaField field, int depth) {
        String name = CharUtil.toCamelCase(field.getName());
        String setter = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        String getter = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        
        String pi = indent(depth);
        String ci = indent(depth + 1);

        buff.append(pi).append("public").append(SPACE).append("void").append(SPACE).append(setter).append("(").append(field.getType().getSimpleName()).append(SPACE).append(name).append(")").append(SPACE).append("{")
                .append(NEW_LINE)
                .append(ci).append("this").append(".").append(name).append(SPACE).append("=").append(SPACE).append(name).append(SEMICOLON)
                .append(NEW_LINE)
                .append(pi).append("}")
                .append(NEW_LINE).append(NEW_LINE);

        buff.append(pi).append("public").append(SPACE).append(field.getType().getSimpleName()).append(SPACE).append(getter).append("(").append(")").append(SPACE).append("{")
                .append(NEW_LINE)
                .append(ci).append("return").append(SPACE).append("this").append(".").append(name).append(SEMICOLON)
                .append(NEW_LINE)
                .append(pi).append("}")
                .append(NEW_LINE).append(NEW_LINE);
    }
    
    private String indent(int depth) {
        char[] indent = new char[depth * 4];
        for (int i = 0; i < indent.length; i ++) {
            indent[i] = ' ';
        }
        return new String(indent);
    }
}
