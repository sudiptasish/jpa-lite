package org.javalabs.jpa.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author schan280
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
