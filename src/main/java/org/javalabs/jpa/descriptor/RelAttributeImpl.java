package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.annotation.LeftOuter;
import org.javalabs.jpa.annotation.RightOuter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link RelAttribute}.
 *
 * @author Sudiptasish Chanda
 */
public class RelAttributeImpl extends EntityAttributeImpl implements RelAttribute {
    
    private Relation relation;
    private List<Join> joins = new ArrayList<>();
    
    private final JoinType joinType;
    
    RelAttributeImpl(Field field) {
        super(field);
        
        // Capture the Relation
        if (field.isAnnotationPresent(OneToOne.class)) {
            OneToOne oto = field.getAnnotation(OneToOne.class);
            relation = new RelationImpl(RelType.OneToOne
                    , oto.cascade()
                    , oto.fetch()
                    , oto.mappedBy());
        }
        else if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany otm = field.getAnnotation(OneToMany.class);
            relation = new RelationImpl(RelType.OneToMany
                    , otm.cascade()
                    , otm.fetch()
                    , otm.mappedBy());
        }
        else if (field.isAnnotationPresent(ManyToOne.class)) {
            ManyToOne mto = field.getAnnotation(ManyToOne.class);
            relation = new RelationImpl(RelType.ManyToOne
                    , mto.cascade()
                    , mto.fetch()
                    , null);
        }
        else if (field.isAnnotationPresent(ManyToMany.class)) {
            ManyToMany mtm = field.getAnnotation(ManyToMany.class);
            relation = new RelationImpl(RelType.ManyToMany
                    , mtm.cascade()
                    , mtm.fetch()
                    , mtm.mappedBy());
        }
        else {
            throw new IllegalArgumentException("Invalid rel type provided for field: " + field.getName());
        }
        
        // If relationship is prsent, then the join column annotation should to be present.
        // If no join column is present, then it will be assumed that the join will
        // performed based on the primary key column(s) of the parent table and the
        // same primary key column(s) (with same name) has to be present in child table.
        JoinColumn join = field.getAnnotation(JoinColumn.class);
        if (join != null) {
            joins.add(new JoinImpl(
                    join.name()
                    , join.table()
                    , join.referencedColumnName()
                    , join.foreignKey() != null && join.foreignKey().value() != ConstraintMode.PROVIDER_DEFAULT));
        }
        JoinColumns joinCols = field.getAnnotation(JoinColumns.class);
        if (joinCols != null) {
            for (JoinColumn jn : joinCols.value()) {
                joins.add(new JoinImpl(
                    jn.name()
                    , jn.table()
                    , jn.referencedColumnName()
                    , jn.foreignKey() != null && jn.foreignKey().value() != ConstraintMode.PROVIDER_DEFAULT));
            }
        }
        
        // Capture the join
        if (field.isAnnotationPresent(LeftOuter.class)) {
            joinType = JoinType.LEFT_OUTER;
        }
        else if (field.isAnnotationPresent(RightOuter.class)) {
            joinType = JoinType.RIGHT_OUTER;
        }
        else {
            joinType = JoinType.INNER;
        }
    }

    @Override
    public Relation relation() {
        return relation;
    }
    
    @Override
    public List<Join> joins() {
        return joins;
    }

    @Override
    public JoinType joinType() {
        return joinType;
    }
    
    public static class RelationImpl implements Relation {
        private final RelType relType;
        private final CascadeType[] cascades;
        private final FetchType fetch;
        private final String mappedBy;

        public RelationImpl(RelType relType, CascadeType[] cascades, FetchType fetch, String mappedBy) {
            this.relType = relType;
            this.cascades = cascades;
            this.fetch = fetch;
            this.mappedBy = mappedBy;
        }

        @Override
        public RelType relType() {
            return relType;
        }

        @Override
        public CascadeType[] cascade() {
            return cascades;
        }

        @Override
        public FetchType fetch() {
            return fetch;
        }

        @Override
        public String mappedBy() {
            return mappedBy;
        }
    }
    
    public static class JoinImpl implements Join {
        private final String joinCol;
        private final String joinTable;
        private final String refCol;
        private final boolean fk;

        public JoinImpl(String joinCol, String joinTable, String refCol, boolean fk) {
            this.joinCol = joinCol;
            this.joinTable = joinTable;
            this.refCol = refCol;
            this.fk = fk;
        }

        @Override
        public String joinColumn() {
            return joinCol;
        }

        @Override
        public String referencedColumn() {
            return refCol;
        }

        @Override
        public boolean fk() {
            return fk;
        }

        @Override
        public String joinTable() {
            return joinTable;
        }
    }
}
