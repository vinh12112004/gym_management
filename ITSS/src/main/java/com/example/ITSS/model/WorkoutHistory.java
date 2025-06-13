package com.example.ITSS.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_history")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WorkoutHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "workout_date")
    private LocalDate workoutDate;

    @Column(name = "note")
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