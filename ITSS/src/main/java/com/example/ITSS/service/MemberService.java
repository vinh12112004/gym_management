// src/main/java/com/example/gymmanagement/service/MemberService.java
package com.example.ITSS.service;

import com.example.ITSS.model.Member;
import com.example.ITSS.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository repo;

    public MemberService(MemberRepository repo) {
        this.repo = repo;
    }

    public List<Member> getAll() {
        return repo.findAll();
    }

    public Member getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public Member create(Member m) {
        return repo.save(m);
    }

    public Member update(Long id, Member m) {
        Member ex = getById(id);
        ex.setName(m.getName());
        ex.setEmail(m.getEmail());
        ex.setPhone(m.getPhone());
        return repo.save(ex);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}