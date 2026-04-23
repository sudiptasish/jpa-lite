package org.javalabs.jpa;

import org.javalabs.jpa.descriptor.EntityAttribute;
import jakarta.persistence.EnumType;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Date;

/**
 * Internal Class used to initiate a database call by setting the bind variables.
 *
 * @author Sudiptasish Chanda
 */
public class BasicJdbcMechanism implements QueryMechanism {

    @Override
    public void prepare(PreparedStatement pStmt
        , int bindIndex
        , EntityAttribute attribute
        , Object bind) throws SQLException {
        
        Class<?> type = attribute.datatype();
        Object val = bind;
        
        // Treat the Enum type separately (compatibility with JPA).
        if (type.isEnum()) {
            Enum enumVal = (Enum)bind;
            if (attribute.enumType() == EnumType.STRING) {
                type = String.class;
                val = enumVal != null ? enumVal.name() : null;
            }
            else {
                type = Integer.class;
                val = enumVal != null ? enumVal.ordinal() : null;
            }
        }
        else if (type == Boolean.class || type == boolean.class) {
            // Convert boolean to integer. (True -> 1, False -> 0)
            type = Integer.class;
            if (bind == null) {
                val = new Integer(0);
            }
            else {
                val = ((Boolean)val) ? new Integer(1) : new Integer(0);
            }
        }
        prepare(pStmt, bindIndex, type, val);
    }
    
    @Override
    public void prepare(PreparedStatement pStmt
            , int bindIndex
            , Class<?> datatype
            , Object bind) throws SQLException {
        
        if (datatype == String.class) {
            if (bind != null) {
                pStmt.setString(bindIndex, (String)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.VARCHAR);
            }
        }
        else if (datatype == Timestamp.class) {
            if (bind != null) {
                //pStmt.setTimestamp(bindIndex, (Timestamp)bind, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
                pStmt.setTimestamp(bindIndex, (Timestamp)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.TIMESTAMP);
            }
        }
        else if (datatype == Integer.class || datatype == int.class) {
            if (bind != null) {
                pStmt.setInt(bindIndex, (Integer)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.NUMERIC);
            }
        }
        else if (datatype == Long.class || datatype == long.class) {
            if (bind != null) {
                pStmt.setLong(bindIndex, (Long)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.NUMERIC);
            }
        }
        else if (datatype.isEnum()) {
            // Default to string type.
            if (bind != null) {
                pStmt.setString(bindIndex, ((Enum)bind).name());
            }
            else {
                pStmt.setNull(bindIndex, Types.VARCHAR);
            }
        }
        else if (datatype == Double.class || datatype == double.class) {
            if (bind != null) {
                pStmt.setDouble(bindIndex, (Double)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.NUMERIC);
            }
        }
        else if (datatype == Float.class || datatype == float.class) {
            if (bind != null) {
                pStmt.setFloat(bindIndex, (Float)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.NUMERIC);
            }
        }
        else if (datatype == Byte.class || datatype == byte.class) {
            if (bind != null) {
                pStmt.setByte(bindIndex, (Byte)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.NUMERIC);
            }
        }
        else if (datatype == Short.class || datatype == short.class) {
            if (bind != null) {
                pStmt.setShort(bindIndex, (Short)bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.NUMERIC);
            }
        }
        else if (datatype == BigDecimal.class) {
            pStmt.setBigDecimal(bindIndex, (BigDecimal)bind);
        }
        else if (datatype == Date.class) {
            pStmt.setDate(bindIndex, (Date)bind);
        }
        else if (datatype == Time.class) {
            pStmt.setTime(bindIndex, (Time)bind);
        }
        else if (datatype == byte[].class) {
            if (bind != null) {
                // InputStream in = new ByteArrayInputStream((byte[])bind);
                // pStmt.setBinaryStream(bindIndex, in, ((byte[])bind).length);
                pStmt.setBytes(bindIndex, (byte[])bind);
            }
            else {
                pStmt.setNull(bindIndex, Types.BINARY);
            }
        }
        else {
            pStmt.setObject(bindIndex, bind);
        }
    }
}
