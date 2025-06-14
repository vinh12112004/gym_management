package com.gymapp.model;

import java.time.LocalDate;

public class MembershipPackage {
    private Long id;
    private Member member;      // gửi object chỉ chứa id
    private Package aPackage;   // gửi object chỉ chứa id
    private Double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private Staff coach;        // gửi object chỉ chứa id, hoặc null

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Package getAPackage() { return aPackage; }
    public void setAPackage(Package aPackage) { this.aPackage = aPackage; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Staff getCoach() { return coach; }
    public void setCoach(Staff coach) { this.coach = coach; }

    public String getMemberName() {
        return member != null ? member.getFullName() : "";
    }

    public String getPackageName() {
        return aPackage != null ? aPackage.getName() : "";
    }
}