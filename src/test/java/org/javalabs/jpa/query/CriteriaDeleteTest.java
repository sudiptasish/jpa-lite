package org.javalabs.jpa.query;

import org.javalabs.jpa.query.CriteriaDelete;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CriteriaDeleteTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaDeleteTest.class);

    @Test
    public void testDelete() {
        CriteriaDelete criteria = new CriteriaDelete();
        criteria.delete();
    }

    @Test
    public void testFrom() {
        CriteriaDelete criteria = new CriteriaDelete();
        criteria.delete();
        criteria.from("ecm_metadata")
                .where("project").eq("amex-eng")
                .and("repo").eq("ecm-example-config")
                .and("repo_type").in(Arrays.asList("STASH", "GITHUB"))
                .and("active").isNotNull()
                .and("status").isNull();
    }

    @Test
    public void testInvalidQueryFormat1() {
        CriteriaDelete criteria = new CriteriaDelete();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.from("ecm_metadata")
                    .where("project").eq("amex-eng");
        });
        Assertions.assertEquals("Invalid query format. Specifying FROM without DELETE clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat2() {
        CriteriaDelete criteria = new CriteriaDelete();
        criteria.delete();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.where("project").eq("amex-eng");
        });
        Assertions.assertEquals("Invalid query format. Specifying WHERE without FROM clause", exception.getMessage());
    }

    @Test
    public void testAnd() {
        CriteriaDelete criteria = new CriteriaDelete();
        criteria.delete();
        criteria.from("ecm_metadata")
                .where("project").eq("amex-eng")
                .and("repo").eq("ecm-example-config")
                .or("onboard_date").between("2015-08-01").and("2016-08-12");
    }

    @Test
    public void testInvalidQueryFormat3() {
        CriteriaDelete criteria = new CriteriaDelete();
        criteria.delete();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.from("ecm_metadata")
                    .and("repo").eq("ecm-example-config")
                    .or("onboard_date").between("2015-08-01").and("2016-08-12");
        });
        Assertions.assertEquals("Invalid query format. Specifying and without where clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat4() {
        CriteriaDelete criteria = new CriteriaDelete();
        criteria.delete();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.from("ecm_metadata")
                    .or("onboard_date").between("2015-08-01").and("2016-08-12");
        });
        Assertions.assertEquals("Invalid query format. Specifying or without where clause", exception.getMessage());
    }

    @Test
    public void testQuery() {
            CriteriaDelete criteria = new CriteriaDelete();
            criteria.delete();
             criteria.from("ecm_metadata")
                    .where("project").eq("amex-eng")
                    .and("repo").eq("ecm-example-config")
                    .and("repo_type").in(Arrays.asList("STASH", "GITHUB"))
                    .and("active").isNotNull()
                    .and("active").like("Y")
                    .and("active").lt("Y")
                    .and("active").gte("Y")
                     .and("active").gt("Y")
                     .and("active").lte("Y")
                    .and("status").isNull();

            List<Object> params = criteria.params();

            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);

    }

}
