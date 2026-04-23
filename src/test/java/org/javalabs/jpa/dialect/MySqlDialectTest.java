package org.javalabs.jpa.dialect;

import org.javalabs.jpa.dialect.MySqlDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MySqlDialectTest {
    @Test
    public void testDataType() {
        MySqlDialect dialect = new MySqlDialect();
        dialect.data_type(Integer.class);
        dialect.query_tables("ecm");
        dialect.run_command("SELECT * FROM EMPLOYEE");
        dialect.timestamp();
        dialect.version();
        dialect.auto_gen_data_type();
    }

    @Test
    public void testException() {
        MySqlDialect dialect = new MySqlDialect();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dialect.data_type(Object.class);
        });
        Assertions.assertEquals("No mysql data type found for java type: Object", exception.getMessage());
    }
}
