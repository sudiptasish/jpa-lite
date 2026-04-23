package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 *
 * @author schan280
 */
@Documented
@Target({TYPE, FIELD})
@Retention(RUNTIME)
public @interface RightOuter {
    
}
