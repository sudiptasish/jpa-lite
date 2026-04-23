package org.javalabs.jpa.txn;

import org.javalabs.jpa.txn.PersistenceContextImpl;
import org.javalabs.jpa.txn.LitePersistenceContext;
import org.javalabs.jpa.LiteEntityManager;
import org.junit.jupiter.api.Test;

public class PersistenceContextImplTest {
    @Test
    public void testem() {
        LiteEntityManager em = null;
        Object owner = null;
        LitePersistenceContext parent = null;
        PersistenceContextImpl obj1 = new PersistenceContextImpl(null, null, null);
        obj1.em();
        obj1.owner();
        obj1.parent();
        obj1.destroy();
    }
}
