package com.example.gym.service;

import com.example.gym.model.Employee;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeService {
    private final Map<Long, Employee> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Employee create(Employee e) {
        long id = seq.getAndIncrement();
        e.setId(id);
        store.put(id, e);
        return e;
    }

    public List<Employee> listAll() {
        return new ArrayList<>(store.values());
    }

    public boolean update(Employee e) {
        if (!store.containsKey(e.getId())) return false;
        store.put(e.getId(), e);
        return true;
    }

    public boolean delete(long id) {
        return store.remove(id) != null;
    }
}