package org.javalabs.jpa.descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a store for named queries.
 * 
 * <p>
 * Manages registration, retrieval, and lifecycle of predefined
 * queries identified by unique names.
 *
 * @author Sudiptasish Chanda
 */
public class NamedQueryStoreImpl implements NamedQueryStore {
    
    private static final NamedQueryStore STORE = new NamedQueryStoreImpl();
    
    private final Map<String, QueryAttribute> queryMap = new HashMap<>();
    
    private NamedQueryStoreImpl() {}
    
    public static NamedQueryStore getStore() {
        return STORE;
    }

    @Override
    public void put(QueryAttribute query) {
        if (queryMap.containsKey(query.name())) {
            throw new IllegalArgumentException(query.name() + " is already defined");
        }
        queryMap.put(query.name(), query);
    }

    @Override
    public QueryAttribute get(String name) {
        return queryMap.get(name);
    }
    
}
