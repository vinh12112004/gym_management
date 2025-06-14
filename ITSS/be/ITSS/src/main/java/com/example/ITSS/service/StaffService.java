package com.example.ITSS.service;

import com.example.ITSS.model.Staff;
import com.example.ITSS.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository repo;

    public StaffService(StaffRepository repo) {
        this.repo = repo;
    }

    public List<Staff> getAll() {
        return repo.findAll();
    }

    public Staff getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
    }

    public Staff create(Staff s) {
        return repo.save(s);
    }

    public Staff update(Long id, Staff s) {
        Staff ex = getById(id);
        ex.setFirstName(s.getFirstName());
        ex.setLastName(s.getLastName());
        ex.setEmail(s.getEmail());
        ex.setPhone(s.getPhone());
        ex.setPosition(s.getPosition());
        return repo.save(ex);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Staff getByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public Staff updateByEmail(String email, Staff updatedStaff) {
        Staff existing = repo.findByEmail(email).orElse(null);
        if (existing == null) return null;
        existing.setFirstName(updatedStaff.getFirstName());
        existing.setLastName(updatedStaff.getLastName());
        existing.setPhone(updatedStaff.getPhone());
        // ...cập nhật các trường khác nếu cần...
        return repo.save(existing);
    }
}