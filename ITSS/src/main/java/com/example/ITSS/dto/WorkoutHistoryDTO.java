package com.example.ITSS.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class WorkoutHistoryDTO {
    private Long memberId;
    private String note;
    private LocalDate workoutDate;

    // Default constructor
    public WorkoutHistoryDTO() {}

    // Constructor với JsonCreator annotation
    @JsonCreator
    public WorkoutHistoryDTO(
            @JsonProperty("memberId") Long memberId,
            @JsonProperty("note") String note,
            @JsonProperty("workoutDate") LocalDate workoutDate) {
        this.memberId = memberId;
        this.note = note;
        this.workoutDate = workoutDate;
    }

    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }
}