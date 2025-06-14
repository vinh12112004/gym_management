package com.example.ITSS.controller;

import com.example.ITSS.model.Feedback;
import com.example.ITSS.model.Member;
import com.example.ITSS.model.User;
import com.example.ITSS.repository.MemberRepository;
import com.example.ITSS.repository.UserRepository;
import com.example.ITSS.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService svc;
    private final UserRepository userRepo;
    private final MemberRepository memberRepo;

    public FeedbackController(FeedbackService svc, UserRepository userRepo, MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
        this.svc = svc;
        this.userRepo = userRepo;

    }

    @GetMapping
    public List<Feedback> all() {
        return svc.getAll();
    }

    @GetMapping("/{id}")
    public Feedback get(@PathVariable Long id) {
        return svc.getById(id);
    }

    @PostMapping
    public Feedback create(@RequestBody Feedback f) {
        // Lấy email của user từ token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Tìm User tương ứng
        User u = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Tìm Member tương ứng với User qua email
        Member member = memberRepo.findByEmail(u.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member not found for this user"));

        // Gán Member object thay vì memberId
        f.setMember(member);
        f.setMemberName(member.getFirstName() + " " + member.getLastName());
        f.setCreatedAt(LocalDateTime.now());

        return svc.create(f);
    }

    @PutMapping("/{id}")
    public Feedback update(@PathVariable Long id, @RequestBody Feedback f) {
        Feedback existing = svc.getById(id);

        // Giữ nguyên member từ bản ghi cũ
        f.setId(id);
        f.setMember(existing.getMember()); // Thay vì setMemberId()
        f.setMemberName(existing.getMemberName());
        f.setCreatedAt(existing.getCreatedAt());
        f.setUpdatedAt(LocalDateTime.now());

        return svc.update(f);
    }
}