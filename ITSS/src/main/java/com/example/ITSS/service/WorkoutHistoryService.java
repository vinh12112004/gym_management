package com.example.ITSS.service;

import com.example.ITSS.model.WorkoutHistory;
import com.example.ITSS.repository.WorkoutHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutHistoryService {
    private final WorkoutHistoryRepository repo;

    public WorkoutHistoryService(WorkoutHistoryRepository repo) {
        this.repo = repo;
    }

    public List<WorkoutHistory> findByMemberId(Long memberId) {
        return repo.findByMemberId(memberId);
    }

    public WorkoutHistory save(WorkoutHistory history) {
        return repo.save(history);
    }
}