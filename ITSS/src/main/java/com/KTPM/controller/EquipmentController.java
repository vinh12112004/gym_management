package com.KTPM.controller;

import com.KTPM.model.Equipment;
import com.KTPM.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipments")
@CrossOrigin("*")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    // Lấy tất cả thiết bị
    @GetMapping
    public List<Equipment> getAllEquipments() {
        return equipmentService.getAllEquipments();
    }

    // Lấy thiết bị theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Integer id) {
        Optional<Equipment> equipment = equipmentService.getEquipmentById(id);
        return equipment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Thêm thiết bị mới
    @PostMapping
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        Equipment savedEquipment = equipmentService.saveEquipment(equipment);
        return new ResponseEntity<>(savedEquipment, HttpStatus.CREATED);
    }

    // Cập nhật thiết bị
    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Integer id, @RequestBody Equipment equipment) {
        return equipmentService.getEquipmentById(id)
                .map(existingEquipment -> {
                    equipment.setId(id);
                    Equipment updatedEquipment = equipmentService.saveEquipment(equipment);
                    return new ResponseEntity<>(updatedEquipment, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Xóa thiết bị
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Integer id) {
        return equipmentService.getEquipmentById(id)
                .map(equipment -> {
                    equipmentService.deleteEquipment(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Tìm kiếm thiết bị theo tên
    @GetMapping("/search/name")
    public List<Equipment> searchEquipmentsByName(@RequestParam String name) {
        return equipmentService.findEquipmentsByName(name);
    }

    // Tìm kiếm thiết bị theo loại
    @GetMapping("/search/type")
    public List<Equipment> searchEquipmentsByType(@RequestParam String type) {
        return equipmentService.findEquipmentsByType(type);
    }

    // Tìm kiếm thiết bị theo trạng thái
    @GetMapping("/search/status")
    public List<Equipment> searchEquipmentsByStatus(@RequestParam String status) {
        return equipmentService.findEquipmentsByStatus(status);
    }

    // Tìm kiếm thiết bị theo vị trí
    @GetMapping("/search/location")
    public List<Equipment> searchEquipmentsByLocation(@RequestParam String location) {
        return equipmentService.findEquipmentsByLocation(location);
    }
}
