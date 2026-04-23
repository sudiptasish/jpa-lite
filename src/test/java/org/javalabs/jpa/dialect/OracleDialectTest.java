package org.javalabs.jpa.dialect;

import org.javalabs.jpa.dialect.OracleDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OracleDialectTest {
    @Test
    public void testDataType() {
        OracleDialect dialect = new OracleDialect();
        dialect.data_type(Integer.class);
        dialect.query_tables("ecm");
        dialect.run_command("SELECT * FROM EMPLOYEE");
        dialect.timestamp();
        dialect.version();
        dialect.auto_gen_data_type();
    }

    @Test
    public void testException() {
        OracleDialect dialect = new OracleDialect();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dialect.data_type(Object.class);
        });
        Assertions.assertEquals("No oracle data type found for java type: Object", exception.getMessage());
    }
}
