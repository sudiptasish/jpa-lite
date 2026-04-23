package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation to identify a data access object.
 *
 * @author Sudiptasish Chanda
 */
@Documented
@Target({TYPE, FIELD})
@Retention(RUNTIME)
public @interface Dao {
    
}
