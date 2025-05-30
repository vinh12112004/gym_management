package com.example.gymmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gymmanagement.model.Package;
import com.example.gymmanagement.service.PackageService;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final PackageService svc;

    public PackageController(PackageService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Package> all() {
        return svc.getAll();
    }

    @GetMapping("/{id}")
    public Package get(@PathVariable Long id) {
        return svc.getById(id);
    }

    @PostMapping
    public Package create(@RequestBody Package p) {
        return svc.create(p);
    }

    @PutMapping("/{id}")
    public Package update(@PathVariable Long id, @RequestBody Package p) {
        return svc.update(id, p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
