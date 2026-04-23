package org.javalabs.jpa.query;

import org.javalabs.jpa.query.CriteriaUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CriteriaUpdateTest {

    @Test
    public void testDelete() {
        CriteriaUpdate criteria = new CriteriaUpdate();
        criteria.update("test_tab");
    }

    @Test
    public void testInvalidQueryFormat1() {
        CriteriaUpdate criteria = new CriteriaUpdate();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.set("status")
                    .where("project").eq("sudiptasish");
        });
        Assertions.assertEquals("Invalid query format. Specifying SET without UPDATE clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat3() {
        CriteriaUpdate criteria = new CriteriaUpdate();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.update("test_tab")
                    .where("project").eq("sudiptasish")
                    .set("status").eq("Y");

        });
        Assertions.assertEquals("Invalid query format. Specifying WHERE without SET clause", exception.getMessage());
    }

   @Test
    public void testUpdate() {
        CriteriaUpdate criteria = new CriteriaUpdate();
        criteria.update("test_tab")
                .set("project").eq("sudiptasish_engg")
                .where("repo").eq("jpa-lite")
                .and("status").eq("SUCCESS")
                .or("onboard_date").between("2015-08-01").and("2016-08-12")
                .and("active").isNotNull()
                .and("active").like("Y")
                .and("active").lt("Y")
                .and("active").gte("Y")
                .and("active").gt("Y")
                .and("active").lte("Y")
                .and("status").isNull();

       List<Object> params = criteria.params();

       String sql = criteria.toQuery();

    }
}
