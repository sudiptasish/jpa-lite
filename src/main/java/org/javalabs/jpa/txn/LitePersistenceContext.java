package org.javalabs.jpa.txn;

import org.javalabs.jpa.LiteEntityManager;
import jakarta.persistence.EntityManager;

/**
 * Interface to represent a persistence context.
 * 
 * <p>
 * The persistence context is the collection of all the managed objects of an 
 * {@link EntityManager}. The main role of the persistence context is to make sure
 * that a database entity object is represented by no more than one in-memory entity
 * object within the same EntityManager. Every EntityManager manages its own
 * persistence context. Therefore, a database object can be represented by different
 * memory entity objects in different {@link EntityManager} instances.
 * 
 * <p>
 * In the context of {@link LiteEntityManager}, nothing is cached in the memory.
 * and object of this class also holds the ownership details of the currently
 * active entity manager.
 *
 * @author Sudiptasish Chanda
 */
public interface LitePersistenceContext {
    
    /**
     * Return the entity manager that is part of this persistence context.
     * @return LiteEntityManager
     */
    LiteEntityManager em();
    
    /**
     * Return the owner of the currently active txn, thus the entity manager.
     * It just provides a reference of the object that started the txn.
     * @return Object
     */
    Object owner();
    
    /**
     * Return the parent context.
     * 
     * Sometimes it is possible for application component to start a new pragma
     * transaction, even if one transaction is in progress. In which case the currently
     * active persistence context has to be backed up, before creaating a new context.
     * The new entity persistence context will thus become a child of it.
     * 
     * @return LitePersistenceContext
     */
    LitePersistenceContext parent();
    
    /**
     * Destroy this persistence context.
     */
    void destroy();
}
