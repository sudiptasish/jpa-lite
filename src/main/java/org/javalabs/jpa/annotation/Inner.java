package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 *
 * @author schan280
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Inner {
    
}
