package com.example.ITSS.repository;

import com.example.ITSS.model.MembershipPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipPackageRepository extends JpaRepository<MembershipPackage, Long> {
    List<MembershipPackage> findByMemberId(Long memberId);
    List<MembershipPackage> findByCoachId(Long coachId);
}