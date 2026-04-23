package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * API that does not support a txn must annotate themselves with this tag.
 *
 * @author Sudiptasish Chanda
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface NotSupported {
    
}
