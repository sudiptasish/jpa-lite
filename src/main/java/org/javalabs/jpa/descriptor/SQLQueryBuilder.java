package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.QueryCache.NativeQuery;
import org.javalabs.jpa.descriptor.QueryCache.QueryType;
import org.javalabs.jpa.descriptor.RelAttribute.RelType;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder class to generate the SQL Query.
 * 
 * This class has specific APIs to generate SELECT and other queries required for
 * database CRUD operation. The queries are generated once and stored in a local
 * cache for future use.
 *
 * @author Sudiptasish Chanda
 */
public class SQLQueryBuilder implements QueryBuilder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLQueryBuilder.class);
    
    private final QueryCache cache = SqlQueryCache.get();
    
    public SQLQueryBuilder() {}

    @Override
    public String selectQuery(Class<?> clazz) {
        return selectQuery(clazz, QueryType.SELECT);
    }

    @Override
    public String selectQuery(Class<?> clazz, QueryType type) {
        NativeQuery nativeQuery = cache.get(clazz, type);
        
        if (nativeQuery == null) {
            StringBuilder query = new StringBuilder(128);
            ClassDescriptor desc = PersistenceHandler.get().getDescriptor(clazz);
            if (desc == null) {
                throw new IllegalArgumentException("Object " + clazz.getName() + " is not a registered jpa entity");
            }
            query.append("SELECT ");
            
            // Now add the remaining columns in the select clause in order.
            List<String> ids = new ArrayList<>(2);
            
            for (Iterator<EntityAttribute> itr = desc.attributes(); itr.hasNext(); ) {
                EntityAttribute attribute = itr.next();
                if (!attribute.isTransient()) {
                    query.append(attribute.column()).append(", ");
                    if (attribute.isId()) {
                        ids.add(attribute.column());
                    }
                }
            }
            query.delete(query.length() - 2, query.length());
            query.append("\n  FROM ").append(desc.table());
            
            cache.put(clazz, QueryType.SELECT_ALL, query.toString());
            
            query.append("\n WHERE ");
            
            for (String id : ids) {
                query.append(id).append(" = ?")
                    .append("\n   AND ");
            }
            query.delete(query.length() - 8, query.length());
            
            // Now put the generated query in the cache for future reuse.
            String selectOnly = query.toString();
            cache.put(clazz, QueryType.SELECT, selectOnly);
            
            // Cache the locking query as well.
            String selectWithLock = selectOnly + " FOR UPDATE";
            cache.put(clazz, QueryType.SELECT_LOCK, selectWithLock);
            
            query.delete(0, query.length());
            ids.clear();
            
            // Build the relationship query.
            // Now, check for mapping.
            List<RelAttribute> rels = new ArrayList<>(4);
            
            // Important!!! Maintain the order.
            // 1 - OneToOne
            // 2 - OneToMany
            
            // Uni-Directional => No ManyToOne or ManyToMany.
            List<RelAttribute> oneToOne = desc.oneToOne();
            if (oneToOne != null) {
                for (RelAttribute rel : oneToOne) {
                    if (rel.relation().fetch() == FetchType.EAGER) {
                        rels.add(rel);
                    }
                }
            }
            List<RelAttribute> oneToMany = desc.oneToMany();
            if (oneToMany != null) {
                for (RelAttribute rel : oneToMany) {
                    if (rel.relation().fetch() == FetchType.EAGER) {
                        rels.add(rel);
                    }
                }
            }
            if (! rels.isEmpty() && rels.size() <= 8) {
                // POJO structures:
                //
                // @Table(name = "employees")
                // class Employee {
                //
                //   @Id
                //   @Column(name = "id")
                //   Integer id;
                //
                //   @Column(name = "emp_name")
                //   String empName;
                //
                //   @OneToMany(fetch = FetchType.EAGER)
                //   @LeftJoin
                //   @JoinColumn(name = "emp_id", mappedBy = "emp")
                //   List<Department> departments;
                //
                // }
                //
                // @Table(name = "departments")
                // class Department {
                //
                //   @Id
                //   @Column(name = "emp_id")
                //   Integer empId;
                //
                //   @Id
                //   @Column(name = "dept_id")
                //   Integer deptId;
                //
                //   @ManyToOne
                //   @JoinColumn(foreignKey = @ForeignKey(), name = "emp_id", table = "employees", referencedColumnName = "id")
                //   Employee emp;
                //
                // }
                //
                // Parent table: employees
                // Child tables: [ department, managers ]
                //
                // Expected SQL Query:
                // SELECT a.emp_id, a.emp_name, b.emp_id, b.dept, c.manager_id, c.project
                //   FROM employees a
                //   LEFT OUTER JOIN departments b ON (a.emp_id = b.emp_id)
                //   
                
                String alias = "a";
                String[] aliases = new String[] {"b", "c", "d", "e", "f", "g", "h", "i"};    // Maximum 8 joins
                
                query.append("SELECT ");
                for (Iterator<EntityAttribute> itr = desc.attributes(); itr.hasNext(); ) {
                    EntityAttribute attribute = itr.next();
                    if (!attribute.isTransient()) {
                        query.append(alias).append(".").append(attribute.column()).append(", ");
                        if (attribute.isId()) {
                            ids.add(attribute.column());
                        }
                    }
                }
                query.delete(query.length() - 2, query.length());
                
                // Query generated (so far): "SELECT a.id, a.emp_name"
                
                RelAttribute rel = null;
                String childAlias = null;
                
                // List 'rels' will always have all OneToOne and OneToMany.
                // Here: It's only OneToMany
                for (int i = 0; i < rels.size(); i ++) {
                    rel = rels.get(i);
                    
                    // rel:
                    // datatype: Department.class (Table - departments)
                    // joinType: JoinType.LEFT_OUTER (LeftJoin)
                    // relation: { relType() : OneToMany, fetch() : EAGER }
                    // joins   : [ { joinColumn() : emp_id } ]
                    
                    // childDesc is the Department entity.
                    childAlias = aliases[i];
                    ClassDescriptor childDesc = PersistenceHandler.get().getDescriptor(rel.datatype());
                    
                    // Getting all columns from departments table...
                    for (Iterator<EntityAttribute> itr = childDesc.attributes(); itr.hasNext(); ) {
                        EntityAttribute attribute = itr.next();
                        if (!attribute.isTransient()) {
                            query.append(", ").append(childAlias).append(".").append(attribute.column());
                        }
                    }
                }
                
                // Query generated (so far): "SELECT a.id, a.emp_name, b.dept_id, b.emp_id"
                
                query.append("\n  FROM ").append(desc.table()).append(" ").append(alias);
                
                // Query generated (so far): "SELECT a.id, a.emp_name, b.dept_id, b.emp_id
                //                              FROM employees a"
                
                for (int i = 0; i < rels.size(); i ++) {
                    rel = rels.get(i);
                    
                    if (rel.relation().mappedBy() == null || rel.relation().mappedBy().trim().length() == 0) {
                        throw new RuntimeException("Parent entity " + desc.entityClass().getSimpleName() + " has " + rel.relation().relType()
                                + " relation to child entity " + rel.datatype().getSimpleName() + ". But no mappedBy attribute is present");
                    }
                    
                    // childDesc is the Department entity.
                    childAlias = aliases[i];
                    ClassDescriptor childDesc = PersistenceHandler.get().getDescriptor(rel.datatype());
                    
                    // joining with departments table...
                    query.append("\n ").append(rel.joinType().syntax()).append(" ").append(childDesc.table()).append(" ").append(childAlias).append(" ON ").append("(");
                    
                    // Query generated (so far): "SELECT a.id, a.emp_name, b.dept_id, b.emp_id
                    //                              FROM employees a
                    //                              .... JOIN departments b ON ("
                
                    // Now take the columns to join on.
                    // Go by the mappedBy attribute of OneToMany/OneToOne from parent table.
                    RelAttribute childRel = null;
                    if (rel.relation().relType() == RelType.OneToOne) {
                        // Expect a OneToOne mapping in child entity as well with the same mappedByName
                        childRel = childDesc.relation(RelType.OneToOne, rel.relation().mappedBy());
                    }
                    else if (rel.relation().relType() == RelType.OneToMany) {
                        // Expect a OneToOne mapping in child entity as well with the same mappedByName
                        childRel = childDesc.relation(RelType.ManyToOne, rel.relation().mappedBy());
                    }
                    if (childRel == null) {
                        throw new RuntimeException("Parent entity " + desc.entityClass().getSimpleName() + " has " + rel.relation().relType()
                                + " relation to child entity " + childDesc.entityClass().getSimpleName() + " with mappedBy " + rel.relation().mappedBy()
                                + ". But child entity  does not have any field called " + rel.relation().mappedBy());
                    }
                    if (childRel.datatype() != desc.entityClass()) {
                        throw new RuntimeException("Parent entity " + desc.entityClass().getSimpleName() + " has " + rel.relation().relType()
                                + " relation to child entity " + childDesc.entityClass().getSimpleName() + " with mappedBy " + rel.relation().mappedBy()
                                + ". But the " + rel.relation().mappedBy() + " attribute in child entity is pointing to " + childRel.datatype().getSimpleName());
                    }
                    
                    int x = 0;
                    for (RelAttribute.Join join : childRel.joins()) {
                        if (join.joinColumn() == null || join.joinTable() == null || join.referencedColumn() == null) {
                            throw new RuntimeException("Either of name, table, referencedColumn is empty");
                        }
                        // The joinColumn should be present in child table.
                        if (! childDesc.hasAttribute(join.joinColumn())) {
                            throw new RuntimeException("Column " + join.joinColumn() + " is specified in " + childRel.relation().relType()
                                    + " relationship in child entity " + childDesc.entityClass().getSimpleName()
                                    + ". But child entity does not have the column " + join.joinColumn());
                        }
                        if (! desc.hasAttribute(join.referencedColumn())) {
                            throw new RuntimeException("Column " + join.joinColumn() + " is specified in " + childRel.relation().relType()
                                    + " relationship in child entity " + childDesc.entityClass().getSimpleName()
                                    + ". But parent entity " + desc.entityClass().getSimpleName() + " does not have the column " + join.joinColumn());
                        }
                        if (! desc.table().equals(join.joinTable())) {
                             throw new RuntimeException("Table " + join.joinTable() + " is specified in " + childRel.relation().relType()
                                    + " relationship in child entity " + childDesc.entityClass().getSimpleName()
                                    + ". But parent entity " + desc.entityClass().getSimpleName() + " is associated to a different table " + desc.table());
                        }
                        
                        if (x > 0) {
                            query.append(" AND ");
                        }
                        query.append(alias).append(".").append(join.referencedColumn())
                                .append(" = ")
                                .append(childAlias).append(".").append(join.joinColumn());
                        
                        x ++;
                    }
                    query.append(")");
                    // Query generated (so far): "SELECT a.id, a.emp_name, b.dept_id, b.emp_id
                    //                              FROM employees a
                    //                              .... JOIN departments b ON (a.id = b.emp_id)"
                }
                cache.put(clazz, QueryType.SELECT_REL_ALL, query.toString());
                
                query.append("\n WHERE ");
                for (String id : ids) {
                    query.append(alias).append(".").append(id).append(" = ?")
                        .append("\n   AND ");
                }
                query.delete(query.length() - 8, query.length());
                
                cache.put(clazz, QueryType.SELECT_REL, query.toString());
            }
            nativeQuery = cache.get(clazz, type);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Prepared Select query for {}. SQL:\n{}"
                        , clazz.getSimpleName()
                        , nativeQuery.raw());
            }
        }
        return nativeQuery.raw();
    }
    
    @Override
    public String insertQuery(Class<?> clazz) {
        NativeQuery nativeQuery = cache.get(clazz, QueryType.INSERT);
        
        if (nativeQuery == null) {
            StringBuilder query = new StringBuilder(128);
            ClassDescriptor desc = PersistenceHandler.get().getDescriptor(clazz);
            
            if (desc == null) {
                throw new IllegalArgumentException("Object " + clazz.getName() + " is not a registered jpa entity");
            }
            query.append("INSERT")
                .append(" INTO ")
                .append(desc.table()).append(" (");
            
            int colCount = 0;
            // Now add the columns in the insert clause in order of appearence.
            for (Iterator<EntityAttribute> itr = desc.attributes(); itr.hasNext(); ) {
                EntityAttribute attribute = itr.next();
                if (!attribute.isTransient() && !attribute.isAutoGenerated()) {
                    query.append(attribute.column()).append(", ");
                    colCount ++;
                }
            }
            query.delete(query.length() - 2, query.length());
            query.append(")\nVALUES (");
            
            for (int i = 0; i < colCount; i ++) {
                query.append("?, ");
            }
            query.delete(query.length() - 2, query.length());
            query.append(")");
            
            // Now put the generated query in the cache for future reuse.
            cache.put(clazz, QueryType.INSERT, query.toString());
            
            nativeQuery = cache.get(clazz, QueryType.INSERT);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Prepared Insert query for {}. SQL:\n{}"
                        , clazz.getSimpleName()
                        , nativeQuery.raw());
            }
        }
        return nativeQuery.raw();
    }
    
    @Override
    public String updateQuery(Class<?> clazz) {
        NativeQuery nativeQuery = cache.get(clazz, QueryType.UPDATE);
        
        if (nativeQuery == null) {
            StringBuilder query = new StringBuilder(64);
            ClassDescriptor desc = PersistenceHandler.get().getDescriptor(clazz);
            
            if (desc == null) {
                throw new IllegalArgumentException("Object " + clazz.getName() + " is not a registered jpa entity");
            }
            List<String> ids = new ArrayList<>(2);
            EntityAttribute versionAttr = null;
            
            query.append("UPDATE ")
                .append(desc.table())
                .append("\n   SET ");
            
            for (Iterator<EntityAttribute> itr = desc.attributes(); itr.hasNext(); ) {
                EntityAttribute attribute = itr.next();
                if (! attribute.isTransient()
                    && attribute.updatable()) {
                    
                    if (attribute.isId()) {
                        ids.add(attribute.column());
                    }
                    else {
                        if (!attribute.isAutoGenerated()) {
                            query.append(attribute.column())
                                .append(" = ?,")
                                .append("\n       ");
                        }
                    }
                    if (attribute.isVersion()) {
                        versionAttr = attribute;
                    }
                }
            }
            query.delete(query.length() - 9, query.length());
            query.append("\n WHERE ");
            
            for (String id : ids) {
                query.append(id)
                    .append(" = ?")
                    .append("\n   AND ");
            }
            if (versionAttr != null) {
                query.append(versionAttr.name())
                    .append(" = ?")
                    .append("\n   AND ");
            }
            query.delete(query.length() - 8, query.length());
            
            // Now put the generated query in the cache for future reuse.
            cache.put(clazz, QueryType.UPDATE, query.toString());
            
            nativeQuery = cache.get(clazz, QueryType.UPDATE);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Prepared Update query for {}. SQL:\n{}"
                        , clazz.getSimpleName()
                        , nativeQuery.raw());
            }
        }
        return nativeQuery.raw();
    }
    
    @Override
    public String deleteQuery(Class<?> clazz) {
        NativeQuery nativeQuery = cache.get(clazz, QueryType.DELETE);
        
        if (nativeQuery == null) {
            StringBuilder query = new StringBuilder(32);
            ClassDescriptor desc = PersistenceHandler.get().getDescriptor(clazz);
            if (desc == null) {
                throw new IllegalArgumentException("Object " + clazz.getName() + " is not a registered jpa entity");
            }
            
            query.append("DELETE FROM ")
                .append(desc.table())
                .append("\n WHERE ");
            
            for (Iterator<EntityAttribute> itr = desc.attributes(); itr.hasNext(); ) {
                EntityAttribute attribute = itr.next();
                if (attribute.isId()) {
                    query.append(attribute.column())
                        .append(" = ?")
                        .append("\n   AND ");
                }
            }
            query.delete(query.length() - 8, query.length());
            
            // Now put the generated query in the cache for future reuse.
            cache.put(clazz, QueryType.DELETE, query.toString());
            
            nativeQuery = cache.get(clazz, QueryType.DELETE);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Prepared Delete query for {}. SQL:\n{}"
                        , clazz.getSimpleName()
                        , nativeQuery.raw());
            }
        }
        return nativeQuery.raw();
    }
}
