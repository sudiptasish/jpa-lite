package org.javalabs.jpa.query;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DB result set handler.
 *
 * @author Sudiptasish Chanda
 */
public class DBResultSetHandler extends ResultSetHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DBResultSetHandler.class);

    DBResultSetHandler() {
        super();
    }

    @Override
    public List<Object> handleResultSet(ResultSet resultSet, Map<String, Object> hints) throws SQLException {		
        return handleResultSet(resultSet, hints, -1);
    }

    @Override
    public List<Object> handleResultSet(ResultSet resultSet
            , Map<String, Object> hints
            , int limit) throws SQLException {
        
        ResultSetMetaData rsMetadata = resultSet.getMetaData();

        String[] columns = new String[rsMetadata.getColumnCount()];
        int[] types = new int[rsMetadata.getColumnCount()];
        int i = 0;

        try {
            for ( ; i < columns.length; i ++) {
                columns[i] = rsMetadata.getColumnName(i + 1);
                types[i] = rsMetadata.getColumnType(i + 1);
            }
        }
        catch (SQLException e) {
            LOGGER.error("Error fetching column metadata for index: " + (i + 1) +
                    ". Column count: " + columns.length +
                    ". MD Column Count: " + rsMetadata.getColumnCount(), e);
            throw e;
        }

        List<Object> list = new ArrayList<>();
        int count = 0;
        
        if ("true".equals(hints.get("fetch.table.metadata"))) {
            list.add(columns);
        }

        while (resultSet.next()) {
            Object[] row = new Object[columns.length];

            for (i = 0; i < columns.length; i ++) {
                switch(types[i]) {
                    case Types.ROWID:											
                    case Types.VARBINARY:
                            row[i] = resultSet.getString(i + 1);
                            break;

                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                            row[i] = resultSet.getString(i + 1);
                            break;

                    case Types.NUMERIC:
                    case Types.INTEGER:
                            Integer val1 = resultSet.getInt(i + 1);
                            if (!resultSet.wasNull()) {
                                row[i] = val1;
                            }
                            break;

                    case Types.DOUBLE:
                    case Types.FLOAT:
                            Double val2 = resultSet.getDouble(i + 1);
                            if (!resultSet.wasNull()) {
                                row[i] = val2;
                            }
                            break;

                    case Types.BIGINT:
                            BigDecimal valBD = resultSet.getBigDecimal(i + 1);
                            BigInteger val3 = null;
                            if (valBD != null) {
                                val3 = valBD.toBigInteger();
                            }
                            if (!resultSet.wasNull()) {
                                // Convert the big integer to int value.
                                // TODO (XXX): Change it later.
                                if (val3 != null) {
                                    try {
                                        int intValue = val3.intValueExact();
                                        row[i] = intValue;
                                    }
                                    catch (ArithmeticException e) {
                                        row[i] = val3;
                                    }
                                }
                            }
                            break;

                    case Types.DATE:
                            row[i] = resultSet.getDate(i + 1);
                            break;

                    case Types.TIME:
                            row[i] = resultSet.getTime(i + 1);
                            break;

                    case Types.TIMESTAMP:
                            row[i] = resultSet.getTimestamp(i + 1);
                            break;

                    case Types.TIMESTAMP_WITH_TIMEZONE:
                            // Not handling OffsetDateTime now.
                            row[i] = resultSet.getTimestamp(i + 1);
                            break;

                    default:
                            row[i] = resultSet.getObject(i + 1);
                            break;
                }
            }
            if (columns.length == 1) {
                list.add(row[0]);
            }
            else {
                list.add(row);
            }

            if (limit != -1 && ++ count == limit) {
                return list;
            }
        }
        return list;
    }

    @Override
    public <T> List<T> handleResultSet(ResultSet resultSet, Class<T> clazz) throws SQLException {
        return handleResultSet(resultSet, clazz, -1);
    }

    @Override
    public <T> List<T> handleResultSet(ResultSet resultSet, Class<T> clazz, Map<String, Object> hints) throws SQLException {
        return handleResultSet(resultSet, clazz, -1, hints);
    }

    @Override
    public <T> List<T> handleResultSet(ResultSet resultSet, Class<T> clazz, int limit) throws SQLException {
        return handleResultSet(resultSet, clazz, limit, EMPTY_HINTS);
    }

    @Override
    public <T> List<T> handleResultSet(ResultSet resultSet
        , Class<T> clazz
        , int limit
        , Map<String, Object> hints) throws SQLException {
        
        RetrievalStrategy strategy = RetrievalStrategy.derive(hints);
        return strategy.fetch(resultSet, clazz, limit, hints);
    }
}