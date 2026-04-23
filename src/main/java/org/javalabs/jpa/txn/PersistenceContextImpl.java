package org.javalabs.jpa.txn;

import org.javalabs.jpa.LiteEntityManager;

/**
 * Platfor provided implementation of {@link LitePersistenceContext}.
 *
 * @author Sudiptasish Chanda
 */
public class PersistenceContextImpl implements LitePersistenceContext {
    
    private final LiteEntityManager em;
    private final Object owner;
    private final LitePersistenceContext parent;
    
    PersistenceContextImpl(LiteEntityManager em
        , Object owner
        , LitePersistenceContext parent) {
        
        this.em = em;
        this.owner = owner;
        this.parent = parent;
    }

    @Override
    public LiteEntityManager em() {
        return em;
    }

    @Override
    public Object owner() {
        return owner;
    }

    @Override
    public LitePersistenceContext parent() {
        return parent;
    }

    @Override
    public void destroy() {
        // TODO
    }
    
}
