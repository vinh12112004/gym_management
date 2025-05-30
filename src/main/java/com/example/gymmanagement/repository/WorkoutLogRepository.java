package com.example.gymmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gymmanagement.model.WorkoutLog;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {}
