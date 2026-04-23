package org.javalabs.jpa;

import org.javalabs.jpa.annotation.Dao;
import org.javalabs.jpa.txn.TransactionalProxy;
import jakarta.persistence.EntityManager;

/**
 * DAO proxy class.
 * 
 * <p>
 * While using <code>jpa-lite</code> in the container architecture, the {@link Dao}
 * classes are instantiated at the time of server startup. jpa-lite ensures to
 * inject a new {@link EntityManager} instance just before any of it's method is
 * called to carry out any transaction. Typically the {@link Dao} classes are used 
 * by the business objects to perform any database operation. Use the {@link #get(java.lang.Class) }
 * method to obtain an instance of the specific {@link Dao} class. You don't need
 * to obtain the instance for every single method call. Once an instance is obtained,
 * you can use it throughout the life-cycle of your application.
 * 
 * Example:
 * {@link Dao} interface:
 * 
 * <pre>
 * {@code 
 * 
 * @Dao
 * public interface EmployeeDAO {
 *     void insert(Employee emp);
 * }
 * </pre>
 * 
 * The corresponding implementation:
 * 
 * <pre>
 * {@code 
 * 
 * public class EmployeeDAOImpl implements EmployeeDAO {
 *     
 *     @PersistenceContext(name = "emp-pu")
 *     private EntityManager em;
 *   
 *     public void insert(Employee emp) {
 *         em.persist(emp);
 *     }
 * }
 * </pre>
 * 
 * Obtain the reference of this EmployeeDAOImpl class in your business object.
 * 
 * <pre>
 * {@code 
 * 
 * public class EmployeeHandler {
 *     
 *     private final EmployeeDAO empDAO;
 *   
 *     public EmployeeHandler() {
 *         this.empDAO = DAOProxy.get(EmployeeDAO.class);
 *     }
 * 
 *     // Other business methods
 * }
 * </pre>
 * 
 * 
 * @author schan280
 */
public class DAOProxy {
    
    /**
     * Obtain an instance of a specific {@link Dao} class as identified by this <code>clazz</code>
     * 
     * @param <T>   Dao class of specific type.
     * @param clazz Class type. Ensure this class has the @Dao annotation.
     * @return T
     */
    public static <T> T get(Class<T> clazz) {
        return (T)TransactionalProxy.instance(clazz);
    }
}
