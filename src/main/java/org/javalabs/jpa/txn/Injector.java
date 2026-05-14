package org.javalabs.jpa.txn;

import java.util.List;

/**
 * Central component responsible for dependency injection and object provisioning.
 *
 * <p>The {@code Injector} manages the creation, configuration, and lifecycle of
 * application objects by resolving their dependencies. It acts as a registry
 * and factory that supplies fully constructed instances based on configured
 * bindings.</p>
 *
 * <p>Typical responsibilities include:</p>
 * <ul>
 *   <li>Resolving dependencies for requested types</li>
 *   <li>Injecting dependencies into constructors, fields, or methods</li>
 *   <li>Managing object scopes (e.g., singleton, prototype)</li>
 *   <li>Providing configured instances based on bindings</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * 
 * <pre>
 * {@code
 * Injector injector = ...;
 * MyService service = injector.getInstance(MyService.class);
 * }
 * </pre>
 *
 * <p>Implementations may support advanced features such as:</p>
 * <ul>
 *   <li>Custom scopes and lifecycle hooks</li>
 *   <li>Lazy or provider-based injection</li>
 *   <li>Annotation-driven configuration</li>
 * </ul>
 *
 * <p>
 * <b>Thread safety:</b> Instances of this class is thread safe
 * </p>
 *
 * <p>
 * <b>Note:</b> The injector should typically be configured once during
 * application startup and reused throughout the application life cycle.
 * </p>
 *
 * @author Sudiptasish Chanda
 */
public interface Injector {
    
    void inject(List<Class> classes);
}
