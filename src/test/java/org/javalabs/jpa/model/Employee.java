package org.javalabs.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@NamedNativeQueries({
    @NamedNativeQuery(name = "Employee.selectAll", query = "SELECT * FROM employees"),
    @NamedNativeQuery(name = "Employee.selectByLocation", query = "SELECT * FROM employees WHERE location = ?")
})
@Table(name="employees")
@IdClass(Employee.EmployeePK.class)
public class Employee implements Serializable {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id", nullable = false)
    private Integer empId;

    @Column(name = "emp_name", nullable = false, length = 64)
    private String empName;

    @Column(name = "location", nullable = false, length = 48)
    private String location;

    @Column(name = "department", nullable = true, length = 32)
    private String department;

    @Column(name = "join_date", nullable = false)
    private Timestamp joinDate;

    public Employee() {
        empId = COUNTER.getAndIncrement();
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    // Define the class that represents the primary key
    @Embeddable
    public static class EmployeePK implements Serializable {
        
        private Integer empId;
        
        public EmployeePK() {}

        public EmployeePK(Integer empId) {
            this.empId = empId;
        }

        public Integer getEmpId() {
            return empId;
        }

        public void setEmpId(Integer empId) {
            this.empId = empId;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.empId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final EmployeePK other = (EmployeePK) obj;
            if (!Objects.equals(this.empId, other.empId)) {
                return false;
            }
            return true;
        }
        
    }
}

