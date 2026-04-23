package org.javalabs.jpa.util;

import java.util.Collection;
import java.util.Map;

/**
 * A general utility class.
 *
 * @author Sudiptasish Chanda
 */
public final class GeneralUtility {
    
    public static boolean isPrimitive(Class<?> type) {
        return type == String.class
            || type == Integer.class
            || type == byte.class
            || type == char.class
            || type == short.class
            || type == int.class
            || type == long.class
            || type == float.class
            || type == double.class
            || type == Byte.class
            || type == Character.class
            || type == Short.class
            || type == Long.class
            || type == Float.class
            || type == Double.class;
    }
    
    public static boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type)
            || Map.class.isAssignableFrom(type);
    }
}
