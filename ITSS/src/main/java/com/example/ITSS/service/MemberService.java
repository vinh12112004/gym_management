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
        ex.setFirstName(m.getFirstName());
        ex.setLastName(m.getLastName());
        ex.setEmail(m.getEmail());
        ex.setPhone(m.getPhone());
        ex.setJoinDate(m.getJoinDate());
        ex.setMembershipType(m.getMembershipType());
        ex.setStatus(m.getStatus());
        return repo.save(ex);
    }
    public Member updateByEmail(String email, Member m) {
        Member existing = repo.findByEmail(email).orElse(null);
        if (existing == null) return null;
        // Cập nhật các trường cần thiết
        existing.setFirstName(m.getFirstName());
        existing.setLastName(m.getLastName());
        existing.setPhone(m.getPhone());
        // ...các trường khác nếu có...
        return repo.save(existing);
    }
    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Member getByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }
}