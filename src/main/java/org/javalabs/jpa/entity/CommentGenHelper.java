package org.javalabs.jpa.entity;

/**
 *
 * @author schan280
 */
public class CommentGenHelper {
 
    private static final String NEW_LINE      = "\n";
    private static final String SPACE         = " ";
    
    private static final String COMMENT_START = "/**";
    private static final String COMMENT_BODY  = " *";
    private static final String COMMENT_END   = " */";
    
    void generateComment(StringBuilder buff, JpaEntity entity, int depth) {
        buff.append(COMMENT_START)
                .append(NEW_LINE)
                .append(COMMENT_BODY).append(SPACE).append(entity.getComment())
                .append(NEW_LINE).append(COMMENT_BODY)
                .append(NEW_LINE).append(COMMENT_BODY).append(SPACE).append("@author").append(SPACE).append(entity.getAuthor())
                .append(NEW_LINE).append(COMMENT_END)
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
