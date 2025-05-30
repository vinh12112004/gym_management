package com.example.gymmanagement.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.gymmanagement.model.WorkoutLog;
import com.example.gymmanagement.repository.WorkoutLogRepository;

@Service
public class WorkoutLogService {

    private final WorkoutLogRepository repo;

    public WorkoutLogService(WorkoutLogRepository repo) {
        this.repo = repo;
    }

    public List<WorkoutLog> getAll() {
        return repo.findAll();
    }

    public WorkoutLog create(Long employeeId, WorkoutLog w) {
        w.setWorkoutDate(Instant.now());
        // assume employee set on w
        return repo.save(w);
    }
}
