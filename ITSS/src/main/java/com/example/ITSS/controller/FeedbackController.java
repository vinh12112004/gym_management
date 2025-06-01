package com.example.ITSS.controller;

import com.example.ITSS.model.Feedback;
import com.example.ITSS.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Feedback createFeedback(@RequestBody Feedback feedback) {
        return feedbackService.saveFeedback(feedback);
    }

    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedbacks();
    }

    @GetMapping("/{id}")
    public Feedback getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFeedback(@PathVariable Long id) {
        Feedback fb = feedbackService.getFeedbackById(id);
        if (fb != null) {
            feedbackService.deleteFeedback(id);
            return new ResponseEntity<>(Map.of("message", "Xóa feedback thành công!"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("message", "Feedback không tồn tại!"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public Feedback updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        return feedbackService.updateFeedback(id, feedback);
    }

    @GetMapping("/user/{userId}")
    public List<Feedback> getFeedbackByUserId(@PathVariable Integer userId) {
        return feedbackService.findByUserId(userId);
    }

    @GetMapping("/target-type/{targetType}")
    public List<Feedback> getFeedbackByTargetType(@PathVariable String targetType) {
        return feedbackService.findByTargetType(targetType);
    }

    @GetMapping("/target/{targetId}")
    public List<Feedback> getFeedbackByTargetId(@PathVariable Integer targetId) {
        return feedbackService.findByTargetId(targetId);
    }
}