package com.example.ITSS.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "membership_package")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MembershipPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // Liên kết với Package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package aPackage;

    @Column(name = "price")
    private Double price;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Liên kết với Staff (coach)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private Staff coach;
}