// src/main/java/com/example/gymmanagement/repository/MemberRepository.java
package com.example.ITSS.repository;

import com.example.ITSS.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {}