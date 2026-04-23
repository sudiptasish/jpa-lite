package org.javalabs.jpa.query;

import org.javalabs.jpa.LiteEntityManager;
import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.EntityAttribute;
import jakarta.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Insert query that will handle all delete operation.
 * 
 * <p>
 * A call to {@link EntityManager#remove(java.lang.Object) } will call this query
 * object to persist the record. {@link DeleteQuery} class internally maintains a
 * cache to hold the in-transit entities, i.e., the one to be persisted. Once the cache is full,
 * or user has explicitly issued a {@link EntityManager#flush() }, the in-memory
 * record(s) will be persisted in underlying database. The cache is introduced in
 * order to reduce the number of DB calls, thus improving the overall performance
 * of the system.
 * 
 * <p>
 * Instance of this class is not threadsafe. The standard practice is, one {@link EntityManager}
 * will always have a single instance of {@link DeleteQuery}.
 *
 * @author Sudiptasish Chanda
 */
public class DeleteQuery extends WriteQuery {
    
    public DeleteQuery(LiteEntityManager em) {
        super(em);
    }

    @Override
    protected String dbQuery() {
        return builder().deleteQuery(entityClass());
    }
    
    @Override
    protected List<List<Object>> prepare(PreparedStatement pStmt) throws SQLException {
        List<List<Object>> params = new ArrayList<>(recordCount());
        List<Object> binds = null;
        Object bind = null;
        int index = 0;
        
        ClassDescriptor desc = handler().getDescriptor(entityClass());
        
        for (Object entity : entities) {
            index = 0;
            
            if (verbose()) {
                binds = new ArrayList<>(desc.idCount());
            }
            for (Iterator<EntityAttribute> itr = desc.ids(); itr.hasNext(); ) {
                EntityAttribute attribute = itr.next();
                bind = handler().get(entity, attribute.column());
                queryMechansim().prepare(pStmt, ++ index, attribute, bind);

                if (verbose()) {
                    binds.add(bind);
                }
            }
            // Framework always facilitates batching.
            pStmt.addBatch();
            
            if (verbose()) {
                params.add(binds);
            }
        }
        return params;
    }
}
