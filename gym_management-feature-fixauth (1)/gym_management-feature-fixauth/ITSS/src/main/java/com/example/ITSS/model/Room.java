package com.example.ITSS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.time.LocalTime;

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
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "roomStatus", nullable = false)
    private RoomStatus roomStatus;

    /**
     * Trường ảo tính tổng số thiết bị trong phòng này.
     * Hibernate sẽ chèn subquery này vào mỗi lần load Room.
     */
    @Formula("(select count(*) from equipment e where e.room_id = id)")
    private int equipmentCount;

    public enum RoomType {
        Gym, Yoga, Fitness
    }

    public enum RoomStatus {
        Available, Occupied, Maintenance, Closed
    }
}