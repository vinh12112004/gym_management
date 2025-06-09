package com.example.ITSS.controller;

import com.example.ITSS.model.Package;
import com.example.ITSS.service.PackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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