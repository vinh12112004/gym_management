package com.KTPM.service;

import com.KTPM.model.Feedback;
import com.KTPM.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Lấy tất cả feedback
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    // Lấy feedback theo ID
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    // Thêm hoặc cập nhật feedback
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Xóa feedback
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    // Lấy feedback theo userId
    public List<Feedback> findByUserId(Integer userId) {
        return feedbackRepository.findByUserId(userId);
    }

    // Lấy feedback theo targetType
    public List<Feedback> findByTargetType(String targetType) {
        return feedbackRepository.findByTargetTypeContainingIgnoreCase(targetType);
    }

    // Lấy feedback theo targetId
    public List<Feedback> findByTargetId(Integer targetId) {
        return feedbackRepository.findByTargetId(targetId);
    }

    public Feedback updateFeedback(Long id, Feedback newFeedback) {
        return feedbackRepository.findById(id)
                .map(fb -> {
                    fb.setContent(newFeedback.getContent());
                    fb.setRating(newFeedback.getRating());
                    fb.setTargetType(newFeedback.getTargetType());
                    fb.setTargetId(newFeedback.getTargetId());
                    fb.setUserId(newFeedback.getUserId());
                    fb.setCreatedAt(newFeedback.getCreatedAt());
                    return feedbackRepository.save(fb);
                })
                .orElse(null);
    }
}