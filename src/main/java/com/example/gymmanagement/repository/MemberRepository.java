// src/main/java/com/example/gymmanagement/repository/MemberRepository.java
package com.example.gymmanagement.repository;

import com.example.gymmanagement.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {}
