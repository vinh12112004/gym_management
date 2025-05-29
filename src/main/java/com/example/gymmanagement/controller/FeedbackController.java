package com.example.gymmanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gymmanagement.model.Feedback;
import com.example.gymmanagement.service.FeedbackService;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService svc;

    public FeedbackController(FeedbackService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Feedback> all() {
        return svc.getAll();
    }

    @PostMapping
    public Feedback create(@RequestBody Feedback f) {
        return svc.create(f.getEmployee().getId(), f);
    }
}
