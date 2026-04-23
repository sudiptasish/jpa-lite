package org.javalabs.jpa.txn;

import org.javalabs.jpa.txn.PUInjector;
import org.javalabs.jpa.PersistenceUnitInfoImpl;
import org.javalabs.jpa.model.EmployeeDAOImpl;
import org.javalabs.jpa.query.CriteriaTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class PUInjectorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaTest.class);

    @Test
    public void testInject() {
        try {

            PUInjector puInj = new PUInjector();
            List<Class> classList = new ArrayList<Class>();
            classList.add(EmployeeDAOImpl.class);
            classList.add(PersistenceUnitInfoImpl.class);
            puInj.inject(classList);
        } catch (Exception e) {
            LOGGER.error("Error in testInject()", e);
            fail(e.getMessage());
        }

    }
}
