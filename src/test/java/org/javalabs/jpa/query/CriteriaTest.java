package org.javalabs.jpa.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class CriteriaTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaTest.class);
    
    @Test
    public void testNoQuery() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testNoQuery()");
            }
            Criteria criteria = new Criteria();
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testNoQuery()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testNoQuery()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery1() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery1()");
            }
            Criteria criteria = new Criteria();
            criteria.select("app_id", "name", "location", "owner", "requester", "created_date", "status", "active", "repo_type")
                    .from("test_tab")
                    .where("name").eq("sudiptasish")
                    .and("location").eq("jpa-lite")
                    .and("repo_type").in(Arrays.asList("STASH", "GITLAB"))
                    .and("active").eq("Y")
                    .and("status").in(Arrays.asList("SUCCESS", "FAILED"))
                    .orderBy("created_date")
                    .desc();
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery1()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery1()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery2() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery2()");
            }
            Criteria criteria = new Criteria();
            criteria.select("employee_id", "emp_name", "salary")
                    .from("test_tab")
                    .where("name").eq("sudiptasish")
                    .and("location").eq("jpa-lite")
                    .or("joining_date").between("2015-08-01").and("2016-08-12")
                    .and("active").eq("Y")
                    .and("status").in(Arrays.asList("SUCCESS", "FAILED"))
                    .orderBy("created_date")
                    .desc();
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery2()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery2()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery3() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery3()");
            }
            Criteria criteria = new Criteria();
            criteria.selectCount("*")
                    .from("test_tab");
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery3()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery3()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery4() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery4()");
            }
            Criteria criteria = new Criteria();
            criteria.selectCount("*")
                    .from("test_tab")
                    .where("name").eq("sudiptasish")
                    .and("location").eq("jpa-lite");
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery4()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery4()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery5() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery5()");
            }
            Criteria criteria = new Criteria();
            criteria.selectDistinct("emp_id")
                    .from("test_tab");
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery5()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery5()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery6() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery6()");
            }
            Criteria criteria = new Criteria();
            criteria.selectCountDistinct("name", "location")
                    .from("test_tab")
                    .where("joining_date").between("2015-08-01").and("2016-08-12")
                    .and("repo_type").in(Arrays.asList("GITLAB"));
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery6()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery6()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery7() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery7()");
            }
            Criteria criteria = new Criteria();
            criteria.select("name", "location", "COUNT(*)")
                    .from("test_tab")
                    .groupBy("name", "location")
                    .having("COUNT(*)").gt(1);
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery7()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery7()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery8() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery8()");
            }
            Criteria criteria = new Criteria();
            criteria.select("name", "location", "COUNT(*)")
                    .from("test_tab")
                    .groupBy("name", "location")
                    .having("COUNT(*)").between(1).and(10);
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery8()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery8()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery9() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery9()");
            }
            Criteria criteria = new Criteria();
            criteria.select("name", "location", "COUNT(*)")
                    .from("test_tab")
                    .groupBy("name", "location")
                    .orderBy("name", "location");
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery9()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery9()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery10() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery10()");
            }
            Criteria criteria = new Criteria();
            criteria.select("name", "location", "COUNT(*)")
                    .from("test_tab")
                    .where("total").lte(5)
                    .groupBy("name", "location")
                    .orderBy("name", "location");
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery10()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery10()", e);
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testQuery11() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery11()");
            }
            Criteria criteria = new Criteria();
            criteria.with("tmp")
                    .as()
                    .select("webhook_id", "branch", "pom_file")
                    .from("test_notifications")
                    .where("branch").eq("dev")
                    .doneWith()
                    .select("id", "branch", "pom_file")
                    .from("tmp")
                    .orderBy("id");
                    
            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery11()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery11()", e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testQuery12() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery6()");
            }
            List<String> columnList = new ArrayList<>();
            columnList.add("name");
            columnList.add("location");
            Criteria criteria = new Criteria();
            criteria.selectCountDistinct(columnList)
                    .from("test_tab")
                    .where("joining_date").between("2015-08-01").and("2016-08-12")
                    .and("repo_type").in(Arrays.asList("GITLAB"));

            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
           assertTrue(true);

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery6()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery6()", e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testQuery13() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery1()");
            }
            Criteria criteria = new Criteria();
            //"employee_id", "emp_name", "salary"
            List<String> columnList = new ArrayList<>();
            columnList.add("name");
            columnList.add("location");
            criteria.select(columnList)
                    .from("test_tab")
                    .where("name").eq("sudiptasish")
                    .and("location").eq("jpa-lite")
                    .and("repo_type").in(Arrays.asList("STASH", "GITHUB"))
                    .and("active").isNotNull()
                    .and("active").like("Y")
                    .and("active").lt("Y")
                    .and("active").gte("Y")
                    .and("status").isNull()
                    .orderBy("created_date")
                    .desc();

            List<Object> params = criteria.params();

            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery1()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery1()", e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testQuery14() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Starting test case: testQuery5()");
            }
            Criteria criteria = new Criteria();
            List<String> columnList = new ArrayList<>();
            columnList.add("emp_id");
            criteria.selectDistinct(columnList)
                    .from("test_tab");

            String sql = criteria.toQuery();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(sql);
            }
            assertTrue(true);

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ended test case: testQuery5()");
            }
        }
        catch (Exception e) {
            LOGGER.error("Error in testQuery5()", e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidQueryFormat1() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.from("test_tab")
                    .where("name").eq("sudiptasish");
        });
        Assertions.assertEquals("Invalid query format. Specifying from without select clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat2() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.where("name").eq("sudiptasish");
        });
        Assertions.assertEquals("Invalid query format. Specifying where without from clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat3() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.select("joining_date")
                    .from("test_tab")
                    .and("location").eq("jpa-lite")
                    .or("joining_date").between("2015-08-01").and("2016-08-12");
        });
        Assertions.assertEquals("Invalid query format. Specifying and without where clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat4() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.select("joining_date")
                    .from("test_tab")
                    .or("joining_date").between("2015-08-01").and("2016-08-12");
        });
        Assertions.assertEquals("Invalid query format. Specifying or without where clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat5() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.select("joining_date")
                    .orderBy("created_date");
        });
        Assertions.assertEquals("Invalid query format. Specifying order by without from clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat6() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.select("joining_date")
                    .from("test_tab")
                    .desc();
        });
        Assertions.assertEquals("Invalid query format. Specifying asc/desc without order by clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat7() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.select("joining_date")
                    .groupBy("created_date");
        });
        Assertions.assertEquals("Invalid query format. Specifying group by without from clause", exception.getMessage());
    }

    @Test
    public void testInvalidQueryFormat8() {
        Criteria criteria = new Criteria();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            criteria.select("joining_date")
                    .having("created_date");
        });
        Assertions.assertEquals("Invalid query format. Specifying having without group by clause", exception.getMessage());
    }
}
