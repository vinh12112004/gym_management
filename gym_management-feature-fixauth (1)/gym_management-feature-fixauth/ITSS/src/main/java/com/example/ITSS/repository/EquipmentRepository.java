package com.example.ITSS.repository;

import com.example.ITSS.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    // Tìm kiếm theo tên (ignore case)
    List<Equipment> findByNameContainingIgnoreCase(String name);

    // Tìm kiếm theo trạng thái
    List<Equipment> findByStatus(String status);

    // Tìm kiếm tất cả thiết bị trong một phòng cụ thể
    List<Equipment> findByRoomId(Long roomId);
}






