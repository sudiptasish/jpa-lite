package org.javalabs.jpa.ds;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Manages a pool of reusable database connections in a lightweight manner.
 * 
 * <p>
 * Provides mechanisms for acquiring and releasing connections, maintaining
 * pool size limits, and ensuring efficient resource utilization.
 * <p>
 * This implementation is designed for simplicity and low overhead, making it
 * suitable for applications with moderate concurrency requirements.
 *
 * @author Sudiptasish Chanda
 */
public final class LiteConnectionPool {
    
    private final BlockingQueue<LitePooledConnection> pool;
    
    LiteConnectionPool() {
        this.pool = new LinkedBlockingQueue<>();
    }
    
    public void add(LitePooledConnection connection) {
        pool.offer(connection);
    }
    
    public LitePooledConnection borrowFromPool(Integer timeout) throws InterruptedException {
        LitePooledConnection connection = pool.poll(timeout, TimeUnit.MILLISECONDS);
        return connection;
    }
    
    public Boolean returnToPool(LitePooledConnection connection) {
        return pool.offer(connection);
    }
    
    public void evictFromPool(LitePooledConnection connection) {
        connection.metrics().setStatus("EVICTED");
        pool.remove(connection);
    }
}
