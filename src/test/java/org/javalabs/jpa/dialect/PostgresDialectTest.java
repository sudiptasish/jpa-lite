package org.javalabs.jpa.dialect;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.model.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PostgresDialectTest {
    @Test
    public void testDataType() {
        PostgresDialect dialect = new PostgresDialect();
        dialect.data_type(Integer.class);
        dialect.query_tables("test_tab");
        dialect.run_command("SELECT * FROM EMPLOYEE");
        dialect.timestamp();
        dialect.version();
        dialect.auto_gen_data_type();
    }

    @Test
    public void testException() {
        PostgresDialect dialect = new PostgresDialect();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dialect.data_type(Object.class);
        });
        Assertions.assertEquals("No postgres data type found for java type: Object", exception.getMessage());
    }

    @Test
    public void testIndex() {
        PostgresDialect dialect = new PostgresDialect();
        Employee emp = new Employee();
        PersistenceHandler persist = PersistenceHandler.get();
        persist.createDescriptor(Employee.class);
        ClassDescriptor desc = persist.getDescriptor(emp);
        dialect.index(desc);
    }
}
