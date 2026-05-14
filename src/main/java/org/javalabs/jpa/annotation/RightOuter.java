package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Represents a RIGHT OUTER join type.
 * 
 * <p>
 * Typically used in query construction or SQL generation to
 * include all records from the right side of a join.
 *
 * @author Sudiptasish Chanda
 */
@Documented
@Target({TYPE, FIELD})
@Retention(RUNTIME)
public @interface RightOuter {
    
}
