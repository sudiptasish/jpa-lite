package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation to identify whether a new transaction will be started.
 *
 * @author Sudiptasish Chanda
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface RequiresNew {
    
}
