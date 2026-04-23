package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.model.Employee;
import org.javalabs.jpa.model.EmployeeDAO;
import org.javalabs.jpa.model.EmployeeDAOImpl;
import org.javalabs.jpa.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PersistenceHandlerTest {
    /*@Test
    public void testNullDescriptor() {
        PersistenceHandler persist = PersistenceHandler.get();
        User user = new User();
        persist.extractPrimaryKey(user);
    }*/

    @Test
    public void testDataType() {
        PersistenceHandler persist = PersistenceHandler.get();
        persist.createDescriptor(Employee.class);

        Employee emp = new Employee();
        Employee emp2 = new Employee();
        persist.copy(emp,emp2);
        persist.columns(emp);
       // persist.extractPrimaryKey(emp);
    }

    public void testNotAJpaEntity() {
        PersistenceHandler persist = PersistenceHandler.get();
        persist.createDescriptor(Employee.EmployeePK.class);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            persist.createDescriptor(Employee.EmployeePK.class);
        });
        Assertions.assertEquals("Class EmployeePK is not a valid jpa entity", exception.getMessage());
    }

    @Test
    public void testDataType3() {
        PersistenceHandler persist = PersistenceHandler.get();
        Employee emp = new Employee();
        EmployeeDAOImpl emp2 = new EmployeeDAOImpl();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            persist.copy(emp,emp2);
        });
        Assertions.assertEquals("From and To class does not match", exception.getMessage());

    }
}
