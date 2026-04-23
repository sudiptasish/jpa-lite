package org.javalabs.jpa.query;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.PersistenceHandler;
import org.javalabs.jpa.descriptor.RelAttribute;
import org.javalabs.jpa.util.QueryHints;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Abstract class to define the column value retrieval strategy.
 * 
 * <p>
 * The ResultSet interface provides getter methods (getBoolean, getLong, and so on)
 * for retrieving column values from the current row. Values can be retrieved using
 * either the index number of the column or the name of the column. In general,
 * using the column index will be more efficient. Columns are numbered from 1.
 * For maximum portability, result set columns within each row should be read in
 * left-to-right order, and each column should be read only once.
 * 
 * JPA-LiTE provides two strategies for fetching column values from within a 
 * {@link ResultSet} object.
 * <ul>
 *   <li>{@link IndexRetrievalStrategy}</li>
 *   <li>{@link NameRetrievalStrategy}</li>
 * </ul>
 * 
 * Both have their own pros aand cons. But baased on the hint provided, appropriate
 * strategy will be picked up at runtime.
 *
 * @author Sudiptasish Chanda
 */
public abstract class RetrievalStrategy {
    
    private static final RetrievalStrategy INDEX_STRATEGY = new IndexRetrievalStrategy();
    
    private static final RetrievalStrategy NAME_STRATEGY = new NameRetrievalStrategy();
    
    protected PersistenceHandler handler = PersistenceHandler.get();
    
    protected RetrievalStrategy() {}
    
    /**
     * Return the appropriate retrieval strategy.
     * Retrieval strategy is based on the query hint provided by client.
     * If no strategy is specified by user, then the default strategy will be returned.
     * 
     * @param hints
     * @return RetrievalStrategy
     */
    public static RetrievalStrategy derive(Map<String, Object> hints) {
        QueryHints.RetrievalStrategy strategy = QueryHints.RetrievalStrategy.NAME;
        
        if (hints != null) {
            strategy = (QueryHints.RetrievalStrategy)
                hints.get(QueryHints.RETRIEVAL_STRATEGY);

            if (strategy == null) {
                strategy = QueryHints.RetrievalStrategy.NAME;
                //strategy = QueryHints.RetrievalStrategy.INDEX;      // Index wise retrieval is faster.
            }
        }
        // Now return the right strategy.
        if (QueryHints.RetrievalStrategy.INDEX == strategy) {
            return INDEX_STRATEGY;
        }
        else if (QueryHints.RetrievalStrategy.NAME == strategy) {
            return NAME_STRATEGY;
        }
        else {
            throw new IllegalArgumentException("Invalid retrieval strategy [" + strategy + "] specified");
        }
    }
    
    /**
     * Return the attribute definition of the relationship mapping (only if the
     * said attribute is present).
     * 
     * @param desc
     * @param hints
     * @return RelAttribute
     */
    protected List<RelAttribute> relAttribute(ClassDescriptor desc, Map<String, Object> hints) {
        List<RelAttribute> rels = null;
        List<String> fetchDefs = null;
        
        Object obj = hints.get(QueryHints.FETCH_DEF);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            fetchDefs = Arrays.asList((String)obj);
        }
        else if (obj instanceof List) {
            fetchDefs = (List<String>)obj;
        }
        
        List<RelAttribute> oneToOnes = null;
        List<RelAttribute> oneToManies = null;
        
        if (fetchDefs != null) {
            rels = new ArrayList<>(fetchDefs.size());
            
            for (String fetchDef : fetchDefs) {
                if (fetchDef.equals("OneToOne")) {
                    if (desc.oneToOne() != null) {
                        oneToOnes = desc.oneToOne();
                    }
                }
                else if (fetchDef.equals("OneToMany")) {
                    if (desc.oneToMany() != null) {
                        oneToManies = desc.oneToMany();
                    }
                }
                else {
                    throw new IllegalArgumentException(
                        "Invalid fetch definition. Supported ones: OneToOne and OneToMany");
                }
            }
            if (oneToOnes != null) {
                rels.addAll(oneToOnes);
            }
            if (oneToManies != null) {
                rels.addAll(oneToManies);
            }
        }
        return rels;
    }
    
    /**
     * Apply the appropriate strategy and fetch the result from the underlying resultset.
     * 
     * @param <T>
     * @param resultSet     The DB result set that will be iterated over to extraact
     *                      the field value(s) and populaate the placeholder.
     * @param clazz         The entity class name.
     * @param limit         Maximum number of records to be returned.
     * @param hints         Optional query hint provided by user.
     * 
     * @return List
     * @throws SQLException 
     */
    protected abstract <T> List<T> fetch(ResultSet resultSet
        , Class<T> clazz
        , int limit
        , Map<String, Object> hints) throws SQLException;
    
    /**
     * Return the appropriate binder object.
     * @param rels
     * @return Binder[]
     */
    protected List<Binder> binder(List<RelAttribute> rels) {
        List<Binder> binders = new ArrayList<>(rels.size());
        
        for (RelAttribute rel : rels) {
            if (rel.relation().relType() == RelAttribute.RelType.OneToOne) {
                binders.add(new OneToOneBinder(RelAttribute.RelType.OneToOne));
            }
            else if (rel.relation().relType() == RelAttribute.RelType.OneToMany) {
                binders.add(new OneToManyBinder(RelAttribute.RelType.OneToMany));
            }
            else {
                throw new IllegalArgumentException("No appropriate binder found for: " + rel.relation().relType());
            }
        }
        return binders;
    }
}
