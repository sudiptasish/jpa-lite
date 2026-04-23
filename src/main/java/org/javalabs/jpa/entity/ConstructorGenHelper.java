package org.javalabs.jpa.entity;

import org.javalabs.jpa.util.CharUtil;
import java.util.List;

/**
 *
 * @author schan280
 */
public class ConstructorGenHelper {
    
    private static final String NEW_LINE      = "\n";
    private static final String SPACE         = " ";
    private static final String SEMICOLON     = ";";
    
    void generateConstructorDef(StringBuilder buff, String className, List<JpaField> fields, int depth, boolean generateParameterized) {
        String pi = indent(depth);
        String ci = indent(depth + 1);

        buff.append(pi).append("public").append(SPACE).append(className).append("()").append(SPACE).append("{}");
        buff.append(NEW_LINE).append(NEW_LINE);

        // Add parameterized constructor
        if (generateParameterized) {
            buff.append(pi).append("public").append(SPACE).append(className).append("(");
            for (int i = 0; i < fields.size(); i ++) {
                buff.append(fields.get(i).getType().getSimpleName()).append(SPACE).append(CharUtil.toCamelCase(fields.get(i).getName()));
                if (i < fields.size() - 1) {
                    buff.append(",").append(SPACE);
                }
            }
            buff.append(")").append(SPACE).append("{");
            buff.append(NEW_LINE);
            for (JpaField pkField : fields) {
                buff.append(ci).append("this").append(".").append(CharUtil.toCamelCase(pkField.getName())).append(SPACE).append("=").append(SPACE).append(CharUtil.toCamelCase(pkField.getName())).append(SEMICOLON);
                buff.append(NEW_LINE);
            }
            buff.append(pi).append("}");
            buff.append(NEW_LINE).append(NEW_LINE);
        }
    }
    
    private String indent(int depth) {
        char[] indent = new char[depth * 4];
        for (int i = 0; i < indent.length; i ++) {
            indent[i] = ' ';
        }
        return new String(indent);
    }
}
