package org.javalabs.jpa;

import org.javalabs.jpa.FactoryStore;
import org.junit.jupiter.api.Test;

public class FactoryStoreTest {

    @Test
    public void testFactoryStore(){
        FactoryStore fact = FactoryStore.getInstance();
        fact.getDefault();
    }
}
