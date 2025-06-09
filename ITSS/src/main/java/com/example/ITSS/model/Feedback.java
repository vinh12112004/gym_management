package com.example.ITSS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID của member gửi feedback
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // Tên member
    @Column(name = "member_name")
    private String memberName;

    // Chủ đề feedback
    @Column(name = "subject")
    private String subject;

    // Nội dung feedback
    @Column(name = "message", nullable = false)
    private String message;

    // Điểm đánh giá
    @Column(name = "rating")
    private Integer rating;

    // Trạng thái feedback
    @Column(name = "status")
    private String status;

    // Thời điểm tạo
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Thời điểm cập nhật
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}