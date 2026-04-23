DROP TABLE IF EXISTS EMPLOYEES;

-- Employee table
CREATE TABLE EMPLOYEES (
    emp_id      NUMBER        NOT NULL,
    emp_name    VARCHAR(64)   NOT NULL,
    location    VARCHAR(48)   NOT NULL,
    department  VARCHAR(32)   ,
    join_date   TIMESTAMP     NOT NULL,
    departmentId NULL
);

-- Primary Key Constraint --
ALTER TABLE EMPLOYEES
ADD CONSTRAINT employees_pk
PRIMARY KEY (emp_id);
