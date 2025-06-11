package com.example.ITSS.service;

import com.example.ITSS.model.Package;
import com.example.ITSS.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PackageService {

    private final PackageRepository repo;

    public PackageService(PackageRepository repo) {
        this.repo = repo;
    }

    public List<Package> getAll() {
        return repo.findAll();
    }

    public Package getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
    }

    public Package create(Package p) {
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return repo.save(p);
    }

    public Package update(Long id, Package p) {
        Package ex = getById(id);
        ex.setName(p.getName());
        ex.setDescription(p.getDescription());
        ex.setPrice(p.getPrice());
        ex.setDurationMonths(p.getDurationMonths());
        ex.setFeatures(p.getFeatures());
        ex.setStatus(p.getStatus());
        ex.setUpdatedAt(LocalDateTime.now());
        return repo.save(ex);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}