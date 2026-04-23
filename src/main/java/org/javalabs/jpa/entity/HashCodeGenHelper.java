package org.javalabs.jpa.entity;

import org.javalabs.jpa.util.CharUtil;
import java.util.List;

/**
 *
 * @author schan280
 */
public class HashCodeGenHelper {
    
    private static final String NEW_LINE      = "\n";
    private static final String TAB           = "    ";
    private static final String SPACE         = " ";
    private static final String SEMICOLON     = ";";
    
    void generateHashCodeDef(StringBuilder buff, List<JpaField> fields, int depth) {
        String pi = indent(depth);
        String ci = indent(depth + 1);

        buff.append(pi).append("@Override").append(NEW_LINE);
        buff.append(pi).append("public").append(SPACE).append("int").append(SPACE).append("hashCode").append("()").append(SPACE).append("{");
        buff.append(NEW_LINE);
        buff.append(ci).append("int").append(SPACE).append("hash")
                .append(SPACE).append("=").append(SPACE)
                .append(7).append(SEMICOLON);
        buff.append(NEW_LINE);

        for (JpaField pkField : fields) {
            String name = CharUtil.toCamelCase(pkField.getName());
            buff.append(ci).append("hash")
                    .append(SPACE).append("=").append(SPACE)
                    .append("71 * hash")
                    .append(SPACE).append("+").append(SPACE)
                    .append("Objects.hashCode").append("(").append("this").append(".").append(name).append(")").append(SEMICOLON);
            buff.append(NEW_LINE);
        }
        buff.append(ci).append("return").append(SPACE).append("hash").append(SEMICOLON);
        buff.append(NEW_LINE);
        buff.append(pi).append("}");
        buff.append(NEW_LINE).append(NEW_LINE);
    }
    
    private String indent(int depth) {
        char[] indent = new char[depth * 4];
        for (int i = 0; i < indent.length; i ++) {
            indent[i] = ' ';
        }
        return new String(indent);
    }
}
