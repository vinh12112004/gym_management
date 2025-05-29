package com.KTPM.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workout_session")
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "exercise", nullable = false)
    private String exercise;

    @Column(name = "duration", nullable = false)
    private LocalTime duration;

    @Column(name = "completion_level", nullable = false)
    private String completionLevel;

    @Column(name = "notes")
    private String notes;

    @Column(name = "user_id", nullable = false)
    private Integer userId;
}