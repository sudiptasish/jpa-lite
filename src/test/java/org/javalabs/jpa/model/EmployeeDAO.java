package org.javalabs.jpa.model;

import org.javalabs.jpa.annotation.Dao;

@Dao
public interface EmployeeDAO {
    
    Employee find(Employee.EmployeePK pk);
    
    void insert(Employee emp);
    
    void update(Employee emp);
    
    void delete(Employee emp);
}