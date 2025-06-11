package com.example.ITSS.controller;

import com.example.ITSS.model.Member;
import com.example.ITSS.service.MemberService;
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

    @GetMapping("/email/{email}")
    public ResponseEntity<Member> getByEmail(@PathVariable String email) {
        Member member = svc.getByEmail(email);
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.notFound().build();
        }
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

    @PutMapping("/email/{email}")
    public ResponseEntity<Member> updateByEmail(@PathVariable String email, @RequestBody Member m) {
        Member updated = svc.updateByEmail(email, m);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}