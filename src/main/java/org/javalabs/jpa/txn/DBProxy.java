package org.javalabs.jpa.txn;

import org.javalabs.jpa.FactoryStore;
import org.javalabs.jpa.UnitOfWork;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Abstract claass for all handlers.
 *
 * @author Sudiptasish Chanda
 */
public abstract class DBProxy {
    
    private EntityManagerFactory emf;
    
    protected DBProxy() { }
    
    /**
     * Initialize the proxy and obtain the entity manager factory.
     * @param puName    Persistence unit name.
     */
    protected void init(String puName) {
        emf = FactoryStore.getInstance().getFactory(puName);
    }
    
    /**
     * Create a new entity manager.
     * 
     * Creation of new {@link EntityManager} indicates the start of a {@link UnitOfWork}.
     * Note that, the new EntityManager is not thread-safe. Therefore individual
     * handler must call this API to obtain a new instance before starting any 
     * database operation. The {@link EntityManagerFactory} is backed by a datasource.
     * Onus is on application client to close the entity manager.
     * If the entity manager is not closed, then it may lead to connection leak,
     * and soon the underlying pool will be exhausted.
     * 
     * @return EntityManager
     */
    protected EntityManager getEm() {
        return emf.createEntityManager();
    }
    
    /**
     * Convenient API to close the {@link EntityManager}.
     * @param em    Entity Manager to be closed.
     */
    protected void closeEm(EntityManager em) {
        if (em != null) {
            em.close();
        }
    }
}
