package com.example.gym.service;

import com.example.gym.model.Feedback;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FeedbackService {
    private final List<Feedback> store = new ArrayList<>();
    private final AtomicLong seq = new AtomicLong(1);

    public List<Feedback> listAll() {
        return new ArrayList<>(store);
    }

    public Feedback add(Feedback f) {
        long id = seq.getAndIncrement();
        f.setId(id);
        store.add(f);
        return f;
    }
}