package org.javalabs.jpa.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Represents an INNER join type.
 * 
 * <p>
 * Used in query construction to return only matching records
 * between joined entities or tables.
 *
 * @author Sudiptasish Chanda
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Inner {
    
}
