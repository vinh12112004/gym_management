package com.example.ITSS.service;

import com.example.ITSS.model.Feedback;
import com.example.ITSS.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository repo;

    public FeedbackService(FeedbackRepository repo) {
        this.repo = repo;
    }

    public List<Feedback> getAll() {
        return repo.findAll();
    }

    public Feedback getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
    }

    public Feedback create(Feedback f) {
        f.setCreatedAt(LocalDateTime.now());
        f.setUpdatedAt(LocalDateTime.now());
        return repo.save(f);
    }

    public Feedback update(Long id, Feedback f) {
        Feedback ex = getById(id);
        // Sửa: dùng setMember() thay vì setMemberId()
        ex.setMember(f.getMember());
        ex.setMemberName(f.getMemberName());
        ex.setSubject(f.getSubject());
        ex.setMessage(f.getMessage());
        ex.setRating(f.getRating());
        ex.setStatus(f.getStatus());
        ex.setUpdatedAt(LocalDateTime.now());
        return repo.save(ex);
    }

    // Hoặc đơn giản hóa method update:
    public Feedback update(Feedback f) {
        f.setUpdatedAt(LocalDateTime.now());
        return repo.save(f);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}