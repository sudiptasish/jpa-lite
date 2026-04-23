package org.javalabs.jpa.query;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link SBPool}.
 *
 * @author schan280
 */
public class SBPoolImpl implements SBPool {
    
    private static volatile SBPool INSTANCE = null;
    
    private static final int DEFAULT_POOL_SIZE = 32;
    
    private static final boolean IDLE = false;
    private static final boolean USED = true;
    private final AtomicBoolean lock = new AtomicBoolean(IDLE);
    
    private final Integer poolSize;
    private final Queue<QueryBuffer> available;
    private final Queue<QueryBuffer> used;
    
    private SBPoolImpl(int poolSize) {
        this.poolSize = poolSize;
        this.available = new LinkedList<>();
        this.used = new LinkedList<>();
        
        for (int i = 0; i < this.poolSize; i ++) {
            this.available.add(new QueryBuffer(384));
        }
    }
    
    public static synchronized SBPool getInstance() {
        return getInstance(DEFAULT_POOL_SIZE);
    }
    
    public static synchronized SBPool getInstance(int poolSize) {
        if (INSTANCE == null) {
            INSTANCE = new SBPoolImpl(poolSize);
        }
        return INSTANCE;
    }

    @Override
    public QueryBuffer lookup() {
        try {
            while (this.lock.compareAndSet(IDLE, USED));
            QueryBuffer buff = null;
            
            if (! this.available.isEmpty()) {
                buff = this.available.remove();
                this.used.add(buff);
            }
            else {
                buff = new QueryBuffer(384);
            }
            return buff;
        }
        finally {
            this.lock.set(IDLE);
        }
    }

    @Override
    public void release(QueryBuffer buff) {
        try {
            while (this.lock.compareAndSet(IDLE, USED));
            
            // Remove the specific QueryBuffer instance going by it's reference.
            // It is highly unlikely that the reference of an object will change,
            // which is why we have the exception handling to identify the cases
            // where it might fail.
            if (this.used.remove(buff)) {
                buff.delete(0, buff.length());
                this.available.add(buff);
            }
            else {
                if (buff.id() <= poolSize) {
                    throw new IllegalStateException("QueryBuffer " + buff.id() + " is not present in used pool");
                }
            }
        }
        finally {
            this.lock.set(IDLE);
        }
    }
    
}
