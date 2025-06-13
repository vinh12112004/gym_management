package com.example.ITSS.repository;

import com.example.ITSS.model.WorkoutHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long> {
    List<WorkoutHistory> findByMemberId(Long memberId);
}
