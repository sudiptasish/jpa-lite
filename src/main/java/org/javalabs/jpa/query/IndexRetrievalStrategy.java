package org.javalabs.jpa.query;

import org.javalabs.jpa.descriptor.ClassDescriptor;
import org.javalabs.jpa.descriptor.EntityAttribute;
import org.javalabs.jpa.descriptor.RelAttribute;
import org.javalabs.jpa.util.ObjectCreationUtil;
import org.javalabs.jpa.util.QueryHints;
import jakarta.persistence.EnumType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that follows index based retrieval strategy.
 * 
 * <p>
 * 
 *
 * @author Sudiptasish Chanda
 */
public class IndexRetrievalStrategy extends RetrievalStrategy {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexRetrievalStrategy.class);

    @Override
    protected <T> List<T> fetch(ResultSet resultSet
        , Class<T> clazz
        , int limit
        , Map<String, Object> hints) throws SQLException {
        
        List<RelAttribute> rels = null;
        Boolean nativeQuery = false;
        List<Binder> binders = null;
        
        ClassDescriptor desc = handler.getDescriptor(clazz);
        Object val = hints.get(QueryHints.ALLOW_NATIVE_QUERY);
        if (val != null) {
            nativeQuery = Boolean.parseBoolean(val.toString());
        }
        if (nativeQuery) {
            rels = relAttribute(desc, hints);
            if (rels != null) {
                binders = binder(rels);
            }
        }
        
        List<T> list = new ArrayList<>();
        Map<RelAttribute.RelType, List<Object>> children = new HashMap<>();
        int count = 0, index = 1, childIndex = 0;

        while (resultSet.next()) {
            T element = ObjectCreationUtil.create(clazz.getName());
            childIndex = fetchInternal(desc, element, resultSet, index);
            list.add(element);

            // Now get the child elements only if the hint is set.
            if (nativeQuery && rels != null) {
                for (RelAttribute rel : rels) {
                    ClassDescriptor childDesc = handler.getDescriptor(rel.datatype());
                    Object childElement = ObjectCreationUtil.create(childDesc.entityClass().getName());

                    childIndex = fetchInternal(childDesc, childElement, resultSet, childIndex);
                    
                    List<Object> tmp = children.get(rel.relation().relType());
                    if (tmp == null) {
                        tmp = new ArrayList<>();
                        children.put(rel.relation().relType(), tmp);
                    }
                    tmp.add(childElement);
                }
            }
            if (limit != -1 && ++ count == limit) {
                return list;
            }
        }
        // Now build the parent-child relationship and populate mapping field.
        if (nativeQuery && !children.isEmpty()) {
            for (Binder binder : binders) {
                List<Object> tmp = children.get(binder.getRelType());
                if (tmp != null && ! tmp.isEmpty()) {
                    RelAttribute rel = null;
                    
                    for (RelAttribute r : rels) {
                        if (binder.getRelType() == r.relation().relType()) {
                            rel = r;
                            break;
                        }
                    }
                    list = binder.bind(list, rel, tmp);
                }
            }
        }
        return list;
    }
    
    private int fetchInternal(ClassDescriptor desc
        , Object element
        , ResultSet resultSet
        , int index) throws SQLException {
        
        EntityAttribute attribute = null;
        
        try {
            for (Iterator<EntityAttribute> itr = desc.attributes(); itr.hasNext(); ) {
                attribute = itr.next();
                Class<?> type = attribute.datatype();
                if (attribute.isTransient()) {
                    continue;
                }
                if (type == String.class) {
                    handler.set(
                        element
                        , attribute.column()    
                        , resultSet.getString(index));
                }
                else if (type == Boolean.class || type == boolean.class) {
                    Integer val = resultSet.getInt(index);
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val == 1);
                    }
                }
                else if (type == Integer.class || type == int.class) {
                    Integer val = resultSet.getInt(index);
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val);
                    }                        
                }
                else if (type == Timestamp.class) {
                    handler.set(
                            element
                            , attribute.column()    
                            , resultSet.getTimestamp(index));
                }
                else if (type == BigDecimal.class) {
                    BigDecimal val = resultSet.getBigDecimal(index);
                    if (!resultSet.wasNull()) {
                        handler.set(
                            element
                            , attribute.column()    
                            , val);
                    }
                }
                else if (type == BigInteger.class) {
                    BigDecimal valBD = resultSet.getBigDecimal(index);
                    BigInteger val = null;
                    if (valBD != null) {
                        val = valBD.toBigInteger();
                    }
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val);
                    }
                }
                else if (type == Long.class || type == long.class) {
                    Long val = resultSet.getLong(index);
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val);
                    }
                }
                else if (type == Double.class || type == double.class) {
                    Double val = resultSet.getDouble(index);
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val);
                    }
                }
                else if (type == Float.class || type == float.class) {
                    Float val = resultSet.getFloat(index);
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val);
                    }
                }
                else if (type == Byte.class || type == byte.class) {
                    Byte val = resultSet.getByte(index);
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val);
                    }
                }
                else if (type == Short.class || type == short.class) {
                    Short val = resultSet.getShort(index);
                    if (!resultSet.wasNull()) {
                        handler.set(element, attribute.column(), val);
                    }
                }
                else if (type == Date.class) {
                    handler.set(
                            element
                            , attribute.column()    
                            , resultSet.getDate(index));
                }
                else if (type == Time.class) {
                    handler.set(
                            element
                            , attribute.column()    
                            , resultSet.getTime(index));
                }
                else if (type.isEnum()) {
                    if (attribute.enumType() == EnumType.STRING) {
                        String val = resultSet.getString(index);
                        if (!resultSet.wasNull()) {
                            handler.set(
                                element
                                , attribute.column()    
                                , Enum.valueOf(type.asSubclass(Enum.class), val));
                        }
                    }
                    else {
                        Integer val = resultSet.getInt(index);
                        if (!resultSet.wasNull()) {
                            handler.set(
                                element
                                , attribute.column()    
                                , type.getEnumConstants()[val]);
                        }
                    }
                }
                else if (type == byte[].class) {
                    handler.set(
                            element
                            , attribute.column()    
                            , resultSet.getBytes(index));
                }
                else {
                    handler.set(
                            element
                            , attribute.column()    
                            , resultSet.getObject(index));
                }
                index ++;
            }
            return index;
        }
        catch (SQLException e) {
            LOGGER.error("Error fetching record"
                    + ". Attribute Name: " + (attribute != null ? attribute.column() : "N/A")
                    + ". Data Type: " + (attribute != null ? attribute.datatype(): "N/A")
                    + ". Field Name: " + (attribute != null ? attribute.name() : "N/A")
                    + ". Entity Class: " + element.getClass()
                    , e);
            
            throw e;
        }
        catch (RuntimeException e) {
            throw new SQLException(e);
        }
    }
}
