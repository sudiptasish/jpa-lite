package org.javalabs.jpa;

import org.javalabs.jpa.JdbcException;
import org.javalabs.jpa.LitePersistenceProvider;
import org.javalabs.jpa.model.Employee;
import org.javalabs.jpa.schema.SchemaGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author schan280
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({JPADBExtension.class})
public class LiteEntityManagerTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LiteEntityManagerTest.class);
    
    private static EntityManagerFactory emf;
    
    @BeforeAll
    public static void init() throws Exception {
        try {
            emf = Persistence.createEntityManagerFactory("jpa-pu");
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Initialized InsertQueryTest. Created Entity Manager Factory: {}", emf);
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in InsertQueryTest::init()", e);
            throw new RuntimeException(e);
        }
    }

    @Order(1)
    @Test
    public void testPersist() {
       EntityManager em = null;
        
        try {
            Employee emp = new Employee();
            emp.setEmpId(101);
            emp.setEmpName("John D'Souza");
            emp.setLocation("Bangalore");
            emp.setDepartment("HR");
            emp.setJoinDate(new Timestamp(System.currentTimeMillis()));
           // Department dep = new Department();
           // emp.setDepartmentId(dep);
            
            // Perform insert.
            em = emf.createEntityManager();
            
            em.getTransaction().begin();
            em.persist(emp);
            em.getTransaction().commit();
            
            //InsertQuery iQuery = new InsertQuery((LiteEntityManager)em);
            //iQuery.addEntityClass(emp.getClass());
            //iQuery.add(emp);
            //iQuery.execute();
            
            // Query the DB.
            Employee e = em.find(Employee.class, new Employee.EmployeePK(101));
            Assertions.assertNotNull(e);

            try {
                //em = emf.createEntityManager();

                Employee employeesingle = em.createNamedQuery("Employee.selectByLocation", Employee.class)   // IN clause
                        .setParameter(1, "Bangalore")
                        .getSingleResult();

                EntityManager finalEm = em;
                NoResultException noResultexception = Assertions.assertThrows(NoResultException.class, () -> {
                    Employee employeeNoResult = finalEm.createNamedQuery("Employee.selectByLocation", Employee.class)   // IN clause
                            .setParameter(1, "Chennai")
                            .getSingleResult();

                });
                Assertions.assertEquals("No matching record found", noResultexception.getMessage());

            } catch (Exception exception) {
                LOGGER.error("LiteEntityManagerTest::testSingleResult failed. ", exception);
                fail(exception.getMessage());
            }
            
            Assertions.assertEquals(emp.getEmpName(), e.getEmpName());
           // Assertions.assertEquals("HR", e.getDepartment());
        }
        catch (Exception e) {
            LOGGER.error("LiteEntityManagerTest::testPersist failed. ", e);
            fail(e.getMessage());
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Order(2)
    @Test
    public void testSelectByLocation() {
        EntityManager em = null;
        
        try {
            em = emf.createEntityManager();
            
            List<Employee> employees = em.createNamedQuery("Employee.selectByLocation", Employee.class)   // IN clause
                .setParameter(1, "Delhi")
                .getResultList();
            
            Assertions.assertTrue(employees.isEmpty());
            
            employees = em.createNamedQuery("Employee.selectByLocation", Employee.class)   // IN clause
                .setParameter(1, "Bangalore")
                .getResultList();
            
            Assertions.assertEquals(1, employees.size());
        }
        catch (Exception e) {
            LOGGER.error("LiteEntityManagerTest::testSelectAll failed. ", e);
            fail(e.getMessage());
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Order(3)
    @Test
    public void testHandleResultSet() {
        EntityManager em = null;

        try {
            LitePersistenceProvider liteProvider = new LitePersistenceProvider();
            liteProvider.generateSchema("jpa-pu", new HashMap<>());

            SchemaGenerator schema = new SchemaGenerator();
            String[] args = {"--dialect"};
            schema.generate(args);
        }
        catch (Exception e) {
            LOGGER.error("LiteEntityManagerTest::testHandleResultSet failed. ", e);
            fail(e.getMessage());
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Order(4)
    @Test
    public void testSingleResult() {
        EntityManager em = null;

        try {
            Employee emp = new Employee();
            emp.setEmpId(201);
            emp.setEmpName("John D'Souza");
            emp.setLocation("Bangalore");
            emp.setDepartment("HR");
            emp.setJoinDate(new Timestamp(System.currentTimeMillis()));

            // Perform insert.
            em = emf.createEntityManager();

            em.getTransaction().begin();
            em.persist(emp);
            em.getTransaction().commit();

            try {
                EntityManager finalEm = em;
                JdbcException jdbcException = Assertions.assertThrows(JdbcException.class, () -> {
                    Employee employeeNoResult = finalEm.createNamedQuery("Employee.selectByLocation", Employee.class)   // IN clause
                            .setParameter(1, "Bangalore")
                            .getSingleResult();
               });
                Assertions.assertEquals("More than one row found", jdbcException.getMessage());

            } catch (Exception exception) {
                LOGGER.error("LiteEntityManagerTest::testSingleResult failed. ", exception);
                fail(exception.getMessage());
            }
        }
        catch (Exception e) {
            LOGGER.error("LiteEntityManagerTest::testPersist failed. ", e);
            fail(e.getMessage());
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Order(5)
    @Test
    public void testTxnError() {

        EntityManager em = emf.createEntityManager();
        Employee emp = new Employee();
        emp.setEmpId(201);
        emp.setEmpName("John D'Souza");


        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            em.persist(emp);
        });
        Assertions.assertEquals("No transaction is started. Or transaction is not active/set to rollback-only", exception.getMessage());

    }

    @Order(5)
    @Test
    public void testTxnError2() {

        EntityManager em = emf.createEntityManager();
        Employee emp = new Employee();
        emp.setEmpId(201);
        emp.setEmpName("John D'Souza");
        em.close();

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            em.persist(emp);
        });
        Assertions.assertEquals("Entity manager is already closed", exception.getMessage());

    }

    @AfterAll
    public static void windup() {
        try {
            if (emf != null) {
                emf.close();
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Executed InsertQueryTest::windup {}");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in InsertQueryTest::windup()", e);
        }
    }
}
