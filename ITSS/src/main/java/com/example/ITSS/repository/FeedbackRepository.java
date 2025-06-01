package com.example.ITSS.repository;


import com.example.ITSS.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Integer userId);

    List<Feedback> findByTargetTypeContainingIgnoreCase(String targetType);

    List<Feedback> findByTargetId(Integer targetId);
}