package com.example.gymmanagement.service;

import com.example.gymmanagement.model.Employee;
import com.example.gymmanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<Employee> getAll() {
        return repo.findAll();
    }

    public Employee getById(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    public Employee create(Employee e) {
        return repo.save(e);
    }

    public Employee update(Long id, Employee e) {
        Employee ex = getById(id);
        ex.setName(e.getName());
        ex.setEmail(e.getEmail());
        ex.setPhone(e.getPhone());
        return repo.save(ex);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
