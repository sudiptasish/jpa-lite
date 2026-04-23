package org.javalabs.jpa.model;

import org.javalabs.jpa.annotation.LeftOuter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="departments")
public class Department implements Serializable {

    @Id
    @Column(name = "dep_id", nullable = false)
    private Integer depId;

    @Column(name = "dep_name", nullable = false)
    private String departmentName;

    @LeftOuter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "department")
    private List<User> user = new ArrayList<>();

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }
}
