package com.example.gymmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gymmanagement.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {}
