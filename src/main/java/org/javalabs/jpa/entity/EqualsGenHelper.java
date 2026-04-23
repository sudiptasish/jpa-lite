package org.javalabs.jpa.entity;

import org.javalabs.jpa.util.CharUtil;
import java.util.List;

/**
 *
 * @author schan280
 */
public class EqualsGenHelper {
    
    private static final String NEW_LINE      = "\n";
    private static final String SPACE         = " ";
    private static final String SEMICOLON     = ";";
    
    void generateEqualsDef(StringBuilder buff, String className, List<JpaField> fields, int depth) {
        String pi = indent(depth);
        String ci = indent(depth + 1);

        buff.append(pi).append("@Override").append(NEW_LINE);
        buff.append(pi).append("public").append(SPACE).append("boolean").append(SPACE).append("equals").append("(").append("Object obj").append(")").append(SPACE).append("{");
        buff.append(NEW_LINE);

        buff.append(ci).append("if").append(SPACE).append("(").append("this == obj").append(")").append("{")
                .append(NEW_LINE)
                .append(indent(depth + 2)).append("return true").append(SEMICOLON)
                .append(NEW_LINE).append(ci).append("}");
        buff.append(NEW_LINE);

        buff.append(ci).append("if").append(SPACE).append("(").append("obj == null").append(")").append("{")
                .append(NEW_LINE)
                .append(indent(depth + 2)).append("return false").append(SEMICOLON)
                .append(NEW_LINE).append(ci).append("}");
        buff.append(NEW_LINE);

        buff.append(ci).append("if").append(SPACE).append("(").append("getClass() != obj.getClass()").append(")").append("{")
                .append(NEW_LINE)
                .append(indent(depth + 2)).append("return false").append(SEMICOLON)
                .append(NEW_LINE).append(ci).append("}");
        buff.append(NEW_LINE);

        buff.append(ci).append("final").append(SPACE).append(className).append(SPACE).append("other").append(SPACE).append("=").append(SPACE).append("(").append(className).append(")").append("obj").append(SEMICOLON)
                .append(NEW_LINE);

        for (int i = 0; i < fields.size(); i ++) {
            String name = CharUtil.toCamelCase(fields.get(i).getName());

            buff.append(ci).append("if").append(SPACE).append("(").append("! Objects.equals(").append("this").append(".").append(name).append(",").append(SPACE).append("other").append(".").append(name).append(")").append(")").append(SPACE).append("{")
                    .append(NEW_LINE)
                    .append(indent(depth + 2)).append("return false").append(SEMICOLON)
                    .append(NEW_LINE).append(ci).append("}");
            buff.append(NEW_LINE);
        }
        buff.append(ci).append("return").append(SPACE).append("true").append(SEMICOLON);
        buff.append(NEW_LINE);
        buff.append(pi).append("}");
    }
    
    private String indent(int depth) {
        char[] indent = new char[depth * 4];
        for (int i = 0; i < indent.length; i ++) {
            indent[i] = ' ';
        }
        return new String(indent);
    }
}
