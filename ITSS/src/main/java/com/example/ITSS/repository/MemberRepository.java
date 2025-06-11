// src/main/java/com/example/gymmanagement/repository/MemberRepository.java
package com.example.ITSS.repository;

import com.example.ITSS.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Transactional
    void deleteByEmail(String email);
    Optional<Member> findByEmail(String email);
}