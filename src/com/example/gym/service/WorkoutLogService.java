package com.example.gym.service;

import com.example.gym.model.WorkoutLog;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class WorkoutLogService {
    private final Map<Long, WorkoutLog> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public WorkoutLog create(WorkoutLog log) {
        long id = seq.getAndIncrement();
        log.setId(id);
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }
        store.put(id, log);
        return log;
    }

    public List<WorkoutLog> listAll() {
        return new ArrayList<>(store.values());
    }
}