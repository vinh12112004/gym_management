package com.example.ITSS.service;

import com.example.ITSS.model.WorkoutSession;
import com.example.ITSS.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutSessionService {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    public List<WorkoutSession> getAllSessions() {
        return workoutSessionRepository.findAll();
    }

    public List<WorkoutSession> getSessionsByUserId(Long userId) {
        return workoutSessionRepository.findByUserId(userId);
    }

    public Optional<WorkoutSession> getSessionById(Long id) {
        return workoutSessionRepository.findById(id);
    }

    public WorkoutSession createSession(WorkoutSession session) {
        return workoutSessionRepository.save(session);
    }

    public WorkoutSession updateSession(Long id, WorkoutSession sessionDetails) {
        return workoutSessionRepository.findById(id)
                .map(session -> {
                    session.setDate(sessionDetails.getDate());
                    session.setExercise(sessionDetails.getExercise());
                    session.setDuration(sessionDetails.getDuration());
                    session.setCompletionLevel(sessionDetails.getCompletionLevel());
                    session.setNotes(sessionDetails.getNotes());
                    session.setUserId(sessionDetails.getUserId());
                    return workoutSessionRepository.save(session);
                })
                .orElse(null);
    }

    public boolean deleteSession(Long id) {
        if (workoutSessionRepository.existsById(id)) {
            workoutSessionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}