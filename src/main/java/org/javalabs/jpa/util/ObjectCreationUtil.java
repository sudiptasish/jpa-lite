package org.javalabs.jpa.util;

import java.lang.reflect.Constructor;

/**
 * Utility class providing helper methods for dynamic object instantiation.
 * 
 * <p>
 * This class typically encapsulates logic for creating objects using reflection,
 * constructors, or factory patterns. It may support instantiation with or without
 * parameters, handling checked exceptions, and simplifying object creation logic
 * across the application.
 * <p>
 * Intended to reduce boilerplate code and centralize object creation strategies.
 *
 * @author Sudiptasish Chanda
 */
public class ObjectCreationUtil {
    
    /**
     * Create and return the instance of the class designated by this className.
     * It uses the no-argument constructor (default one) while instantiating
     * the object (provided the no-argument constructor is defined).
     * 
     * @param   <T>         Class type
     * @param   className   Class name
     * 
     * @return  T
     */
    @SuppressWarnings("unchecked")
	public static <T> T create(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className.trim());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        try {
            Constructor<T> constructor = (Constructor<T>)clazz.getDeclaredConstructor(new Class[] {});
            T obj = constructor.newInstance(new Object[] {});
            return obj;
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException)e.getCause();
            }
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Create and return the instance of the class designated by this className.
     * It uses the specific parameterized constructor while instantiating the
     * class instance.
     * 
     * @param   <T>         Class type
     * @param   className   Class name
     * @param paramTypes    The parameter types, in case it's a parameterized constructor
     * @param params        The value of the parameters.
     * 
     * @return  T
     */
    @SuppressWarnings("unchecked")
	public static <T> T create(String className
                               , Class[] paramTypes
                               , Object[] params) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className.trim());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        try {
            Constructor<T> constructor = (Constructor<T>)clazz.getDeclaredConstructor(paramTypes);
            T obj = constructor.newInstance(params);
            return obj;
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException)e.getCause();
            }
            throw new RuntimeException(e);
        }
    }
}
