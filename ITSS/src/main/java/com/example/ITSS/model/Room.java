package com.example.ITSS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "roomType", nullable = false)
    private RoomType roomType;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "open_time")
    private java.time.LocalTime OpenTime;

    @Column(name = "close_time")
    private java.time.LocalTime CloseTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "roomStatus", nullable = false)
    private RoomStatus roomStatus;

    public enum RoomType {
        Gym, Yoga, Fitness
    }

    public enum RoomStatus {
        Hoat_dong, Sua_chua, Ngung_hoat_dong
    }
}