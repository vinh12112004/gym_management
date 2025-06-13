package com.example.ITSS.controller;

import com.example.ITSS.model.Staff;
import com.example.ITSS.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
public class StaffController {

    private final StaffService svc;

    public StaffController(StaffService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Staff> all() {
        return svc.getAll();
    }

    @GetMapping("/{id}")
    public Staff get(@PathVariable Long id) {
        return svc.getById(id);
    }

    @PostMapping
    public Staff create(@RequestBody Staff staff) {
        return svc.create(staff);
    }

    @PutMapping("/{id}")
    public Staff update(@PathVariable Long id, @RequestBody Staff staff) {
        return svc.update(id, staff);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Staff> getByEmail(@PathVariable String email) {
        Staff staff = svc.getByEmail(email);
        if (staff != null) {
            return ResponseEntity.ok(staff);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/email/{email}")
    public ResponseEntity<Staff> updateByEmail(@PathVariable String email, @RequestBody Staff staff) {
        Staff updated = svc.updateByEmail(email, staff);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}