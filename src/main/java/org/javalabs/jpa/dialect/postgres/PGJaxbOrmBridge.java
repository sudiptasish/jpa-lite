package org.javalabs.jpa.dialect.postgres;

import org.javalabs.jpa.orm.jaxb.AttributesType;
import org.javalabs.jpa.orm.jaxb.BasicType;
import org.javalabs.jpa.orm.jaxb.ColumnType;
import org.javalabs.jpa.orm.jaxb.EntityMappingsType;
import org.javalabs.jpa.orm.jaxb.EntityType;
import org.javalabs.jpa.orm.jaxb.GeneratedValueType;
import org.javalabs.jpa.orm.jaxb.IdType;
import org.javalabs.jpa.orm.jaxb.NamedNativeQueriesType;
import org.javalabs.jpa.orm.jaxb.NamedNativeQueryType;
import org.javalabs.jpa.orm.jaxb.TableType;
import org.javalabs.jpa.util.CharUtil;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author schan280
 */
public class PGJaxbOrmBridge {
    
    public String covertToXml(List<TableMetadata> tables, Map<String, Object> props) {
        String excludePrefix = (String)props.get("exclude.entity.name.prefix");
        String pkgName = (String)props.get("jpa-lite.entity.package");
        
        EntityMappingsType mapping = new EntityMappingsType();
        if (pkgName != null) {
            mapping.setPackage(pkgName);
        }
        Map<String, ColumnMetadata> colMapping = new HashMap<>();
        
        for (TableMetadata tableMD : tables) {
            for (ColumnMetadata columnMD : tableMD.getColumns()) {
                colMapping.put(columnMD.getColumnName(), columnMD);
            }
            
            // Represent the corresponding entity class.
            EntityType ormEntity = new EntityType();
            
            // Add the associated table name.
            TableType table = new TableType();
            table.setName(tableMD.getTableName());
            ormEntity.setTable(table);
            
            String tempTab = tableMD.getTableName();
            if (excludePrefix != null && excludePrefix.trim().length() > 0) {
                if (tempTab.startsWith(excludePrefix)) {
                    tempTab = tempTab.substring(excludePrefix.length());
                }
            }
            // Setup the entity class.
            String singular = CharUtil.singular(tempTab);
            ormEntity.setClazz(pkgName + "." + singular);
            ormEntity.setName(singular);
            
            // Add named-native-query
            NamedNativeQueryType nnQuery = new NamedNativeQueryType();
            nnQuery.setName(singular + ".selectAll");
            nnQuery.setQuery("SELECT * FROM " + tableMD.getTableName());
            
            NamedNativeQueriesType nnQueries = new NamedNativeQueriesType();
            nnQueries.getNamedNativeQuery().add(nnQuery);
            ormEntity.setNamedNativeQueries(nnQueries);
            
            AttributesType attrs = new AttributesType();
            
            // Set the primary key columns.
            List<IdType> idFields = new ArrayList<>();
            for (PrimaryKeyMetadata pkMD : tableMD.getPkColumns()) {
                ColumnMetadata colMD = colMapping.get(pkMD.getColumnName());
                GeneratedValueType genVal = null;
                
                if (colMD != null && colMD.getColumnDefault() != null && colMD.getColumnDefault().startsWith("nextval(")) {
                    // Auto generated column.
                    genVal = new GeneratedValueType();
                    genVal.setStrategy("IDENTITY");
                }
                
                IdType id = new IdType();
                id.setName(CharUtil.toCamelCase(colMD.getColumnName()));
                if (genVal != null) {
                    id.setGeneratedValue(genVal);
                }
                
                ColumnType col = new ColumnType();
                col.setName(pkMD.getColumnName());
                
                setColumnType(colMD, id, col);
                id.setColumn(col);
                idFields.add(id);
            }
            attrs.getId().addAll(idFields);
            
            // Set the other key columns.
            List<BasicType> basicFields = new ArrayList<>();
            
            outer:
            for (ColumnMetadata colMD : tableMD.getColumns()) {
                // If the columns is a primary key field,, it has already been added.
                // Therefore discard this and dump rest of the columns.
                for (IdType id : idFields) {
                    if (id.getColumn().getName().equals(colMD.getColumnName())) {
                        continue outer;
                    }
                }
                BasicType basic = new BasicType();
                basic.setName(CharUtil.toCamelCase(colMD.getColumnName()));
                
                ColumnType col = new ColumnType();
                col.setName(colMD.getColumnName());
                col.setNullable(Boolean.toString("YES".equalsIgnoreCase(colMD.getIsNullable())));
                col.setUpdatable(Boolean.toString("YES".equalsIgnoreCase(colMD.getIsUpdatable())));
                
                setColumnType(colMD, basic, col);
                
                // Populate the check constraint ...
                for (ConstraintMetadata cns : tableMD.getCheckColumns()) {
                    if (cns.getTableName().equals(tableMD.getTableName()) && cns.getColumnName().equals(colMD.getColumnName())) {
                        // Decrypt the checkClause attribute to form the sql check constraint clause.
                        // E.g., ((role)::text = ANY ((ARRAY['CUSTOMER'::character varying, 'PROFESSIONAL'::character varying, 'ADMIN'::character varying])::text[]))
                        //       ==> CHECK (role IN ('CUSTOMER', 'PROFESSIONAL', 'ADMIN'))
                        
                        String regExp = "ARRAY\\[(.*?)\\]";
                        Pattern pattern = Pattern.compile(regExp);
                        Matcher matcher = pattern.matcher(cns.getCheckClause());
                        if (matcher.find()) {
                            String fullMatch = matcher.group(0);        // ARRAY[...]
                            String innerContent = matcher.group(1);     // inside the brackets

                            String[] arr = innerContent.split(",");
                            String[] rs = new String[arr.length];
                            
                            for (int i = 0; i < arr.length; i ++) {
                                rs[i] = arr[i].split("::")[0].trim();
                                if (rs[i].charAt(0) == '(' && rs[i].charAt(rs[i].length() - 1) == ')') {
                                    rs[i] = rs[i].substring(1, rs[i].length() - 1);
                                }
                            }
                            col.setCheck(col.getName() + " IN " + "(" + String.join(", ", rs) + ")");
                            basic.setType(Enum.class.getName());
                        }
                    }
                }
            
                basic.setColumn(col);
                basicFields.add(basic);
            }
            attrs.getBasic().addAll(basicFields);
            ormEntity.setAttributes(attrs);
            mapping.getEntity().add(ormEntity);
            
            // cleanup ...
            colMapping.clear();
        }
        return toXml(mapping);
    }
    
    public String toXml(EntityMappingsType mapping) {
        try {
            JAXBContext context = JAXBContext.newInstance(EntityMappingsType.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
            marshaller.marshal(mapping, out);
            out.flush();
            
            return new String(out.toByteArray());
        }
        catch (IOException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setColumnType(ColumnMetadata colMD, IdType id, ColumnType col) {
        if (colMD.getDataType().equalsIgnoreCase("smallint")) {
            id.setType(Short.class.getName());
            col.setPrecision(colMD.getNumericPrecision());
        }
        else if (colMD.getDataType().equalsIgnoreCase("integer") || colMD.getDataType().equalsIgnoreCase("int")) {
            id.setType(Integer.class.getName());
            col.setPrecision(colMD.getNumericPrecision());
        }
        else if (colMD.getDataType().equalsIgnoreCase("bigint")) {
            //id.setType(BigInteger.class.getName());
            id.setType(Long.class.getName());
            col.setPrecision(colMD.getNumericPrecision());
        }
        else if (colMD.getDataType().equalsIgnoreCase("numeric")) {
            if (colMD.getNumericScale() == 0) {
                id.setType(BigInteger.class.getName());
            }
            else {
                id.setType(BigDecimal.class.getName());
            }
            col.setPrecision(colMD.getNumericPrecision());
            col.setScale(colMD.getNumericScale());
        }
        else if (colMD.getDataType().equalsIgnoreCase("character varying") || colMD.getDataType().equalsIgnoreCase("varchar")) {
            id.setType(String.class.getName());
            col.setLength(colMD.getCharacterMaximumLength());
        }
        else if (colMD.getDataType().equalsIgnoreCase("date")) {
            id.setType(Date.class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("timestamp without time zone")) {
            id.setType(Timestamp.class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("time without time zone")) {
            id.setType(Time.class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("bytea")) {
            id.setType(byte[].class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("text")) {
            id.setType(String.class.getName());
            col.setLength(1000000000);
        }
        else if (colMD.getDataType().equalsIgnoreCase("ARRAY")) {
            id.setType(String[].class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("real")) {
            id.setType(Double.class.getName());
        }
        else {
            System.err.println("Unknown field " + colMD.getDataType() + " for " + colMD.getColumnName() + " in table " + colMD.getTableName());
            id.setType(Object.class.getName());
        }
    }
    
    public void setColumnType(ColumnMetadata colMD, BasicType basic, ColumnType col) {
        if (colMD.getDataType().equalsIgnoreCase("smallint")) {
            basic.setType(Short.class.getName());
            col.setPrecision(colMD.getNumericPrecision());
        }
        else if (colMD.getDataType().equalsIgnoreCase("integer") || colMD.getDataType().equalsIgnoreCase("int")) {
            basic.setType(Integer.class.getName());
            col.setPrecision(colMD.getNumericPrecision());
        }
        else if (colMD.getDataType().equalsIgnoreCase("bigint")) {
            basic.setType(BigInteger.class.getName());
            col.setPrecision(colMD.getNumericPrecision());
        }
        else if (colMD.getDataType().equalsIgnoreCase("numeric")) {
            if (colMD.getNumericScale() == 0) {
                basic.setType(BigInteger.class.getName());
            }
            else {
                basic.setType(BigDecimal.class.getName());
            }
            col.setPrecision(colMD.getNumericPrecision());
            col.setScale(colMD.getNumericScale());
        }
        else if (colMD.getDataType().equalsIgnoreCase("character varying") || colMD.getDataType().equalsIgnoreCase("varchar")) {
            basic.setType(String.class.getName());
            col.setLength(colMD.getCharacterMaximumLength());
        }
        else if (colMD.getDataType().equalsIgnoreCase("date")) {
            basic.setType(Date.class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("timestamp without time zone")) {
            basic.setType(Timestamp.class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("time without time zone")) {
            basic.setType(Time.class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("bytea")) {
            basic.setType(byte[].class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("text")) {
            basic.setType(String.class.getName());
            col.setLength(1000000000);
        }
        else if (colMD.getDataType().equalsIgnoreCase("ARRAY")) {
            basic.setType(String[].class.getName());
        }
        else if (colMD.getDataType().equalsIgnoreCase("real")) {
            basic.setType(Double.class.getName());
        }
        else {
            System.err.println("Unknown field " + colMD.getDataType() + " for " + colMD.getColumnName() + " in table " + colMD.getTableName());
            basic.setType(Object.class.getName());
        }
    }
}
