package org.javalabs.jpa.txn;

import org.javalabs.jpa.txn.TransactionalProxy;
import org.javalabs.jpa.JPADBExtension;
import org.javalabs.jpa.model.Employee;
import org.javalabs.jpa.model.EmployeeDAO;
import org.javalabs.jpa.model.EmployeeDAOImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Timestamp;

@ExtendWith({JPADBExtension.class})
public class TransactionalProxyTest {

    @Test
    public void testNewInstance() {

        EmployeeDAOImpl obj1 = new EmployeeDAOImpl();
        Employee emp1 = new Employee();
        emp1.setEmpId(673);
        emp1.setEmpName("ABC");
        emp1.setLocation("Bangalore");
        emp1.setJoinDate(new Timestamp(System.currentTimeMillis()));

        EmployeeDAO proxy = (EmployeeDAO) TransactionalProxy.newInstance(obj1);
        EmployeeDAO proxy2 = (EmployeeDAO) TransactionalProxy.instance(EmployeeDAOImpl.class);
        proxy.hashCode();
        proxy.insert(emp1);
        proxy.delete(emp1);
    }

}



