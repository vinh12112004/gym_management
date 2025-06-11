package com.example.ITSS.controller;

import com.example.ITSS.model.Feedback;
import com.example.ITSS.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public Feedback get(@PathVariable Long id) {
        return svc.getById(id);
    }

    @PostMapping
    public Feedback create(@RequestBody Feedback f) {
        return svc.create(f);
    }

    @PutMapping("/{id}")
    public Feedback update(@PathVariable Long id, @RequestBody Feedback f) {
        return svc.update(id, f);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}