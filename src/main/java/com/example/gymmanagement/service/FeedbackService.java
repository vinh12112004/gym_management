package com.example.gymmanagement.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.gymmanagement.model.Feedback;
import com.example.gymmanagement.repository.FeedbackRepository;

@Service
public class FeedbackService {

    private final FeedbackRepository repo;

    public FeedbackService(FeedbackRepository repo) {
        this.repo = repo;
    }

    public List<Feedback> getAll() {
        return repo.findAll();
    }

    public Feedback create(Long employeeId, Feedback f) {
        f.setCreatedAt(Instant.now());
        // assume employee field already set on f
        return repo.save(f);
    }
}
