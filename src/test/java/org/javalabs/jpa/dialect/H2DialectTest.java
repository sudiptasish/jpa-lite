package org.javalabs.jpa.dialect;

import org.javalabs.jpa.dialect.H2Dialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class H2DialectTest {
    @Test
    public void testDataType() {
        H2Dialect dialect = new H2Dialect();
        dialect.data_type(Integer.class);
        dialect.query_tables("ecm");

        dialect.run_command("SELECT * FROM EMPLOYEE");
        dialect.timestamp();
        dialect.version();
        dialect.auto_gen_data_type();
    }

    @Test
    public void testException() {
        H2Dialect dialect = new H2Dialect();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dialect.data_type(Object.class);
        });
        Assertions.assertEquals("No postgres data type found for java type: Object", exception.getMessage());
    }
}
