package com.example.ITSS.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "roles")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // OWNER, MANAGER, TRAINER, MEMBER
}