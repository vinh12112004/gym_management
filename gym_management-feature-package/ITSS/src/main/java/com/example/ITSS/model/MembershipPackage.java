package com.example.ITSS.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "membership_package")
@Data
public class MembershipPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "price")
    private Double price;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

}