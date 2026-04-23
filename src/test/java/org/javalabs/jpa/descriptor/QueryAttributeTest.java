package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.QueryAttribute;
import org.junit.jupiter.api.Test;

public class QueryAttributeTest {

    @Test
    public void testQueryAttribute() {
        QueryAttribute query = new QueryAttribute();

        query.setName("EcmQuery");
        query.setQuery("Select * from Employees");
        query.setNativeSql(Boolean.TRUE);
        Boolean nativeSql = query.nativeSql();

    }
}
