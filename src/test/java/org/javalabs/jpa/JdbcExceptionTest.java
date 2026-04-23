package org.javalabs.jpa;

import org.javalabs.jpa.JdbcException;
import org.junit.jupiter.api.Test;

public class JdbcExceptionTest {

    @Test
    public void testJdbcException() {
        JdbcException jdbc = new JdbcException();
        JdbcException jdbc1 = new JdbcException("Exception Message", null);
        Throwable cause = new Throwable();
        JdbcException jdbc2 = new JdbcException(cause);
    }
}
