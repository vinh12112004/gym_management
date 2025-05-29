// src/main/java/com/example/gymmanagement/controller/MemberController.java
package com.example.gymmanagement.controller;

import com.example.gymmanagement.model.Member;
import com.example.gymmanagement.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService svc;

    public MemberController(MemberService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Member> all() {
        return svc.getAll();
    }

    @GetMapping("/{id}")
    public Member get(@PathVariable Long id) {
        return svc.getById(id);
    }

    @PostMapping
    public Member create(@RequestBody Member m) {
        return svc.create(m);
    }

    @PutMapping("/{id}")
    public Member update(@PathVariable Long id, @RequestBody Member m) {
        return svc.update(id, m);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
