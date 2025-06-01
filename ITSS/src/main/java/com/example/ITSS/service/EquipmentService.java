package com.example.ITSS.service;

import com.example.ITSS.model.Equipment;
import com.example.ITSS.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    // Lấy tất cả thiết bị
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    // Lấy thiết bị theo ID
    public Optional<Equipment> getEquipmentById(Integer id) {
        return equipmentRepository.findById(id);
    }

    // Thêm hoặc cập nhật thiết bị
    public Equipment saveEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    // Xóa thiết bị
    public void deleteEquipment(Integer id) {
        equipmentRepository.deleteById(id);
    }

    // Tìm kiếm thiết bị theo tên
    public List<Equipment> findEquipmentsByName(String name) {
        return equipmentRepository.findByNameContainingIgnoreCase(name);
    }

    // Tìm kiếm thiết bị theo loại
    public List<Equipment> findEquipmentsByType(String type) {
        return equipmentRepository.findByTypeContainingIgnoreCase(type);
    }

    // Tìm kiếm thiết bị theo trạng thái
    public List<Equipment> findEquipmentsByStatus(String status) {
        return equipmentRepository.findByStatus(status);
    }

    // Tìm kiếm thiết bị theo vị trí
    public List<Equipment> findEquipmentsByLocation(String location) {
        return equipmentRepository.findByLocationContainingIgnoreCase(location);
    }
}