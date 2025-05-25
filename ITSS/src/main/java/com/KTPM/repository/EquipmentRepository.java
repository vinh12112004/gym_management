package com.KTPM.repository;

import com.KTPM.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    // Tìm kiếm theo tên
    List<Equipment> findByNameContainingIgnoreCase(String name);

    // Tìm kiếm theo loại
    List<Equipment> findByTypeContainingIgnoreCase(String type);

    // Tìm kiếm theo trạng thái
    List<Equipment> findByStatus(String status);

    // Tìm kiếm theo vị trí
    List<Equipment> findByLocationContainingIgnoreCase(String location);
}