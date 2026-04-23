package org.javalabs.jpa;

import org.javalabs.jpa.DAOProxy;
import org.javalabs.jpa.annotation.Dao;
import org.javalabs.jpa.model.EmployeeDAO;
import org.junit.jupiter.api.Test;

public class DAOProxyTest {

    @Test
    public void testGet() {
        DAOProxy proxy = new DAOProxy();
         DAOProxy.get(EmployeeDAO.class);

    }
}
