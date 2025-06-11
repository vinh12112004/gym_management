package com.example.ITSS.controller;

import com.example.ITSS.model.MembershipPackage;
import com.example.ITSS.service.MembershipPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership_packages")
public class MembershipPackageController {
    private final MembershipPackageService service;

    public MembershipPackageController(MembershipPackageService service) {
        this.service = service;
    }

    @GetMapping
    public List<MembershipPackage> getAll() {
        return service.findAll();
    }

    @GetMapping("/member/{memberId}")
    public List<MembershipPackage> getByMember(@PathVariable Long memberId) {
        return service.findByMemberId(memberId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipPackage> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MembershipPackage create(@RequestBody MembershipPackage mp) {
        return service.save(mp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembershipPackage> update(@PathVariable Long id, @RequestBody MembershipPackage mp) {
        return service.findById(id)
                .map(existing -> {
                    mp.setId(id);
                    return ResponseEntity.ok(service.save(mp));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}