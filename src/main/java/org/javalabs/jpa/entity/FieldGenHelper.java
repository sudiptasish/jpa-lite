package org.javalabs.jpa.entity;

import org.javalabs.jpa.util.CharUtil;
import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 *
 * @author schan280
 */
public final class FieldGenHelper {
    
    private static final String NEW_LINE      = "\n";
    private static final String SPACE         = " ";
    private static final String SEMICOLON     = ";";
    
    private static final String STD_COL_TEMPLATE    = "(name = \"{0}\")";
    private static final String CHAR_COL_TEMPLATE   = "(name = \"{0}\", nullable = {1}, insertable = true, updatable = true, length = {2, number, #})";
    private static final String NUM_COL_TEMPLATE    = "(name = \"{0}\", nullable = {1}, insertable = true, updatable = true, precision = {2, number, #}, scale = {3, number, #})";
    
    void generateFieldDef(StringBuilder buff, JpaField field, boolean includeAnnotation, int depth) {
        String indent = indent(depth);
        
        if (includeAnnotation) {
            if (field.getId()) {
                buff.append(indent).append("@Id").append(NEW_LINE);
            }
            if (field.getType() == String.class) {
                buff.append(indent).append("@Column")
                        .append(MessageFormat.format(CHAR_COL_TEMPLATE, field.getName(), field.getNullable(), field.getLength()))
                        .append(NEW_LINE);
            }
            else if (field.getType() == Byte.class
                    || field.getType() == Short.class
                    || field.getType() == Integer.class
                    || field.getType() == Long.class) {

                buff.append(indent).append("@Column")
                        .append(MessageFormat.format(NUM_COL_TEMPLATE, field.getName(), field.getNullable(), field.getPrecision(), 0))
                        .append(NEW_LINE);
            }
            else if (field.getType() == Float.class
                    || field.getType() == Double.class
                    || field.getType() == BigDecimal.class) {

                buff.append(indent).append("@Column")
                        .append(MessageFormat.format(NUM_COL_TEMPLATE, field.getName(), field.getNullable(), field.getPrecision(), field.getScale()))
                        .append(NEW_LINE);
            }
            else {
                buff.append(indent).append("@Column")
                        .append(MessageFormat.format(STD_COL_TEMPLATE, field.getName()))
                        .append(NEW_LINE);
            }
        }
        buff.append(indent).append("private").append(SPACE).append(field.getType().getSimpleName()).append(SPACE).append(CharUtil.toCamelCase(field.getName())).append(SEMICOLON);
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
