package org.javalabs.jpa.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores and manages metrics collected from lightweight components such as
 * connection pools or data sources.
 * 
 * <p>
 * Provides facilities for recording, aggregating, and retrieving metrics data.
 * This class may support in-memory storage with optional extensibility for
 * external monitoring systems.
 * <p>
 * Designed for low-overhead metric collection and reporting.
 *
 * @author Sudiptasish Chanda
 */
public class LiteMetricsStore {
    
    private static final LiteMetricsStore INSTANCE = new LiteMetricsStore();
    
    private final ConcurrentMap<String, ConnectionMetrics> metricsMapping = new ConcurrentHashMap<>();
    
    private LiteMetricsStore() {}
    
    public static LiteMetricsStore get() {
        return INSTANCE;
    }
    
    public void addMetrics(ConnectionMetrics metrics) {
        metricsMapping.put(metrics.getName(), metrics);
    }
    
    public List<ConnectionMetrics> getMetrics() {
        return new ArrayList<>(metricsMapping.values());
    }
}
