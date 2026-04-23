package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.NamedQueryStoreImpl;
import org.javalabs.jpa.descriptor.NamedQueryStore;
import org.javalabs.jpa.descriptor.QueryAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NamedQueryStoreImplTest {

    @Test
    public void testDataType() {
        NamedQueryStore query = NamedQueryStoreImpl.getStore();
        QueryAttribute queryAttribute = new QueryAttribute("test1","Select * from Employees",Boolean.TRUE);
        QueryAttribute queryAttribute2 = new QueryAttribute("test1","Select * from Employees",Boolean.TRUE);
        query.put(queryAttribute);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            query.put(queryAttribute2);
        });
        Assertions.assertEquals("test1 is already defined", exception.getMessage());

    }
}
