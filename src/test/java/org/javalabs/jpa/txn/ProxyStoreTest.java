package org.javalabs.jpa.txn;

import org.javalabs.jpa.txn.PersistenceContextImpl;
import org.javalabs.jpa.txn.ProxyStore;
import org.junit.jupiter.api.Test;

public class ProxyStoreTest {

    @Test
    public void testPut() {

        ProxyStore proxy = ProxyStore.getInstance();
        proxy.put(PersistenceContextImpl.class, proxy);
        proxy.get(PersistenceContextImpl.class);
    }
}
