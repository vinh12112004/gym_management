package com.gymapp.model;

import java.time.LocalDate;


public class WorkoutHistory {
    private Long id;
    private Member member;
    private LocalDate workoutDate;
    private String note;

    public WorkoutHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; } 
    public void setMember(Member member) { this.member = member; }

    public LocalDate getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}