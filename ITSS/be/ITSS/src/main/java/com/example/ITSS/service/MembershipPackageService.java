package com.example.ITSS.service;

import com.example.ITSS.model.MembershipPackage;
import com.example.ITSS.repository.MembershipPackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipPackageService {
    private final MembershipPackageRepository repository;

    public MembershipPackageService(MembershipPackageRepository repository) {
        this.repository = repository;
    }

    public List<MembershipPackage> findAll() {
        return repository.findAll();
    }

    public List<MembershipPackage> findByMemberId(Long memberId) {
        return repository.findByMemberId(memberId);
    }

    public Optional<MembershipPackage> findById(Long id) {
        return repository.findById(id);
    }

    public MembershipPackage save(MembershipPackage mp) {
        return repository.save(mp);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<MembershipPackage> findByCoachId(Long coachId) {
    return repository.findByCoachId(coachId);
}
}