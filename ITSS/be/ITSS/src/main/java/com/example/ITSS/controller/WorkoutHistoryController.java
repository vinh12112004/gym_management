package com.example.ITSS.controller;

import com.example.ITSS.dto.WorkoutHistoryDTO;
import com.example.ITSS.model.Member;
import com.example.ITSS.model.WorkoutHistory;
import com.example.ITSS.service.MemberService;
import com.example.ITSS.service.WorkoutHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout_history")
public class WorkoutHistoryController {
    private final WorkoutHistoryService workoutHistoryService;
    private final MemberService memberService;

    // Sửa constructor - inject cả 2 service
    public WorkoutHistoryController(WorkoutHistoryService workoutHistoryService,
                                    MemberService memberService) {
        this.workoutHistoryService = workoutHistoryService;
        this.memberService = memberService;
    }

    @GetMapping("/member/{memberId}")
    public List<WorkoutHistory> getByMember(@PathVariable Long memberId) {
        return workoutHistoryService.findByMemberId(memberId); // Sửa từ 'service' thành 'workoutHistoryService'
    }

    @PostMapping // Sửa từ "/api/workout_history" thành không có gì (vì class đã có @RequestMapping)
    public ResponseEntity<?> createWorkoutHistory(@RequestBody WorkoutHistoryDTO dto) {
        try {
            // Tìm member theo ID
            Member member = memberService.getById(dto.getMemberId());
            if (member == null) {
                return ResponseEntity.badRequest().body("Member not found");
            }

            // Tạo WorkoutHistory
            WorkoutHistory workoutHistory = new WorkoutHistory();
            workoutHistory.setMember(member);
            workoutHistory.setWorkoutDate(dto.getWorkoutDate());
            workoutHistory.setNote(dto.getNote());

            // Save và return
            WorkoutHistory saved = workoutHistoryService.save(workoutHistory);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating workout history: " + e.getMessage());
        }
    }
}