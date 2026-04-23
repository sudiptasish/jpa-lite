package org.javalabs.jpa.dialect;

import org.javalabs.jpa.dialect.DB2Dialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DB2DialectTest {
    @Test
    public void testDataType() {
        DB2Dialect dialect = new DB2Dialect();
        dialect.data_type(Integer.class);
        dialect.query_tables("ecm");
        dialect.run_command("SELECT * FROM EMPLOYEE");
        dialect.timestamp();
        dialect.version();
        dialect.auto_gen_data_type();
    }

    @Test
    public void testException() {
        DB2Dialect dialect = new DB2Dialect();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dialect.data_type(Object.class);
        });
        Assertions.assertEquals("No db2 data type found for java type: Object", exception.getMessage());
    }
}
