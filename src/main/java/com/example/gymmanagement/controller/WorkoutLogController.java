package com.example.gymmanagement.controller;

import com.example.gymmanagement.model.WorkoutLog;
import com.example.gymmanagement.service.WorkoutLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutLogController {

    private final WorkoutLogService svc;

    public WorkoutLogController(WorkoutLogService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<WorkoutLog> all() {
        return svc.getAll();
    }

    @PostMapping
    public WorkoutLog create(@RequestBody WorkoutLog w) {
        return svc.create(w.getEmployee().getId(), w);
    }
}
