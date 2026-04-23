package org.javalabs.jpa.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class EmployeeDAOImpl implements EmployeeDAO {

    @PersistenceContext(name = "jpa-pu")
    private EntityManager em;

    public Employee find(Employee.EmployeePK pk) {
        return em.find(Employee.class, pk);
    }

    public void insert(Employee emp) {
        em.persist(emp);
    }

    @Override
    public void update(Employee emp) {
        em.merge(emp);
    }

    @Override
    public void delete(Employee emp) {
        em.remove(emp);
    }
}
