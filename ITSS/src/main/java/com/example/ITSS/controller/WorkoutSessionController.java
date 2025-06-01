package com.example.ITSS.controller;

import com.example.ITSS.model.WorkoutSession;
import com.example.ITSS.service.WorkoutSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workout-sessions")
public class WorkoutSessionController {

    @Autowired
    private WorkoutSessionService workoutSessionService;

    @GetMapping
    public List<WorkoutSession> getAllSessions() {
        return workoutSessionService.getAllSessions();
    }

    @GetMapping("/user/{userId}")
    public List<WorkoutSession> getSessionsByUserId(@PathVariable Long userId) {
        return workoutSessionService.getSessionsByUserId(userId);
    }

    @GetMapping("/{id}")
    public Optional<WorkoutSession> getSessionById(@PathVariable Long id) {
        return workoutSessionService.getSessionById(id);
    }

    @PostMapping
    public WorkoutSession createSession(@RequestBody WorkoutSession session) {
        return workoutSessionService.createSession(session);
    }

    @PutMapping("/{id}")
    public WorkoutSession updateSession(@PathVariable Long id, @RequestBody WorkoutSession sessionDetails) {
        return workoutSessionService.updateSession(id, sessionDetails);
    }

    @DeleteMapping("/{id}")
    public boolean deleteSession(@PathVariable Long id) {
        return workoutSessionService.deleteSession(id);
    }
}