package org.javalabs.jpa.txn;

import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
public interface Injector {
    
    void inject(List<Class> classes);
}
