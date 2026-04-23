package org.javalabs.jpa.txn;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the class where all the proxy objects will be kept.
 * 
 * <p>
 * The proxy store keeps all the dao proxy instances. It maintains the mapping
 * between the dao interface and the proxy instance. The proxy instance would
 * already have the other dao injected (if needed be).
 *
 * @author Sudiptasish Chanda
 */
public class ProxyStore {
    
    private static final ProxyStore INSTANCE = new ProxyStore();
    
    // Mapping ...
    private final Map<Class, Object> proxyMap = new HashMap<>();
    
    private ProxyStore() {}
    
    public static ProxyStore getInstance() {
        return INSTANCE;
    }
    
    public void put(Class clazz, Object proxy) {
        proxyMap.put(clazz, proxy);
    }
    
    public Object get(Class clazz) {
        return proxyMap.get(clazz);
    }
}
