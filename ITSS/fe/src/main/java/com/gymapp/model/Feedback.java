package com.gymapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignore field "member" từ backend
public class Feedback {
    private Long id;
    private Long memberId;
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
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    
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