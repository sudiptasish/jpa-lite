package org.javalabs.jpa.dialect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SybaseDialectTest {
    @Test
    public void testDataType() {
        SybaseDialect dialect = new SybaseDialect();
        dialect.data_type(Integer.class);
        dialect.query_tables("test_tab");
        dialect.run_command("SELECT * FROM EMPLOYEE");
        dialect.timestamp();
        dialect.version();
        dialect.auto_gen_data_type();
    }

    @Test
    public void testException() {
        SybaseDialect dialect = new SybaseDialect();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dialect.data_type(Object.class);
        });
        Assertions.assertEquals("No sybase data type found for java type: Object", exception.getMessage());
    }
}
