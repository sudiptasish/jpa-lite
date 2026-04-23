package org.javalabs.jpa.descriptor;

import org.javalabs.jpa.descriptor.RelAttributeImpl;
import org.javalabs.jpa.model.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class RelAttributeImplTest {
    @Test
    public void testInvalidRelType() {

        Field[] fields = Employee.class.getDeclaredFields();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RelAttributeImpl relAttribute = new RelAttributeImpl(fields[0]);
        });
        Assertions.assertEquals("Invalid rel type provided for field: COUNTER", exception.getMessage());
    }
    /*@Test
    public void testrelAttributeImplJoinImpl() {

        RelAttributeImpl.JoinImpl relJoin = new RelAttributeImpl.JoinImpl("empId", "Employees", "empId", Boolean.TRUE);
        relJoin.joinColumn();
        relJoin.fk();
        relJoin.joinTable();
        relJoin.referencedColumn();
    }
    @Test
    public void testRelAttributeImplRelationImpl() {
        CascadeType[] cascades = {CascadeType.ALL};
        RelAttribute.JoinType leftOuter = RelAttribute.JoinType.LEFT_OUTER;

        RelAttributeImpl.RelationImpl relRelationImpl;
        relRelationImpl = new RelAttributeImpl.RelationImpl(RelAttribute.RelType.OneToOne, cascades,
                FetchType.LAZY, "Employee");
        relRelationImpl.relType();
        relRelationImpl.cascade();
        relRelationImpl.fetch();
        relRelationImpl.mappedBy();
    }*/

}
