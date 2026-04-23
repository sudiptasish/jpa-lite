package org.javalabs.jpa.txn;

import org.javalabs.jpa.txn.TxnEntityManagerDelegate;
import org.javalabs.jpa.JPADBExtension;
import org.javalabs.jpa.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Persistence;

import java.sql.Timestamp;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
@ExtendWith({JPADBExtension.class})
public class TxnEntityManagerDelegateTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TxnEntityManagerDelegateTest.class);

    private static EntityManagerFactory emf;
    private static TxnEntityManagerDelegate txnDelegate;
    
    @BeforeAll
    public static void setup() {
        System.setProperty("orm.config.file", "persistence-test.xml");
        
        emf = Persistence.createEntityManagerFactory("jpa-pu", new Properties());
        EntityManager em = emf.createEntityManager();

        txnDelegate = new TxnEntityManagerDelegate();
        txnDelegate.setProxy(em);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created test entity manager factory: {} and entity manager: {}", emf, em);
        }
    }
    
    @Test
    public void testPersist() {
        Employee emp1 = new Employee();

        {
            emp1.setEmpId(673);
            emp1.setEmpName("ABC");
            emp1.setLocation("Bangalore");
            emp1.setJoinDate(new Timestamp(System.currentTimeMillis()));

            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            txnDelegate.setProxy(em);
            txnDelegate.persist(emp1);
            Boolean valid = txnDelegate.contains(emp1);
            txnDelegate.merge(emp1);
            txnDelegate.flush();
            txnDelegate.setFlushMode(FlushModeType.COMMIT);
            FlushModeType flushMode = txnDelegate.getFlushMode();
            txnDelegate.createNamedQuery("Employee.selectAll");
            txnDelegate.createNamedQuery("Employee.selectAll", Employee.class);
            txnDelegate.createNativeQuery("Employee.selectAll");
            txnDelegate.createNativeQuery("Employee.selectAll", Employee.class);
            txnDelegate.joinTransaction();
            Boolean trans = txnDelegate.isJoinedToTransaction();
            Boolean isOpen = txnDelegate.isOpen();
            txnDelegate.getTransaction();
            txnDelegate.getEntityManagerFactory();
            txnDelegate.getDelegate();
            txnDelegate.getMetamodel();


            txnDelegate.detach(emp1);
            txnDelegate.clear();
            txnDelegate.remove(emp1);


            em.close();


        }
    }
}
