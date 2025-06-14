package com.example.ITSS.repository;

import com.example.ITSS.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    @Transactional
    void deleteByEmail(String email);
    Optional<Staff> findByEmail(String email);
    
}