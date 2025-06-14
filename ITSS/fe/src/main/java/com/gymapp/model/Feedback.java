package com.gymapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feedback {
    private Long id;
    private Member member; // nhận member object từ backend
    private String memberName;
    private String subject;
    private String message;
    private Integer rating;
    private String status;

    @JsonDeserialize(using = DateTimeArrayDeserializer.class)
    private LocalDateTime createdAt;

    @JsonDeserialize(using = DateTimeArrayDeserializer.class)
    private LocalDateTime updatedAt;

    public Feedback() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Thêm hàm này để lọc memberId dễ dàng
    public Long getMemberId() {
        return member != null ? member.getId() : null;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", memberName='" + memberName + '\'' +
                ", subject='" + subject + '\'' +
                ", rating=" + rating +
                ", status='" + status + '\'' +
                '}';
    }
}