package org.javalabs.jpa.util;

import java.io.IOException;
import java.util.List;

/**
 * A class scanner.
 * 
 * <p>
 * Attempts to list all the classes in the specified package as determined by
 * the context class loader. 
 *
 * @author Sudiptasish Chanda
 */
public interface ClassScanner {
    
    /**
     * Attempts to list all the classes in the specified package as determined by
     * the context class loader. 
     * 
     * @param packageName
     * @return Class[]
     */
    List<Class> scan(String[] packageNames) throws IOException, ClassNotFoundException;
}
