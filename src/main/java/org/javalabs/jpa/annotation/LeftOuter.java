package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Represents a LEFT OUTER join type.
 * 
 * <p>
 * Typically used in query construction to include all records
 * from the left side of a join.
 *
 * @author Sudiptasish Chanda
 */
@Documented
@Target({TYPE, FIELD})
@Retention(RUNTIME)
public @interface LeftOuter {
    
}
