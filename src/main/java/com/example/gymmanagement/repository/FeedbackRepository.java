package com.example.gymmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gymmanagement.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {}
