package com.example.ITSS.controller;

import com.example.ITSS.model.Equipment;
import com.example.ITSS.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.Map;


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
    public ResponseEntity<Map<String, String>> deleteEquipment(@PathVariable Integer id) {
        return equipmentService.getEquipmentById(id)
                .map(equipment -> {
                    equipmentService.deleteEquipment(id);
                    return new ResponseEntity<>(Map.of("message", "Xóa thiết bị thành công!"), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(Map.of("message", "Thiết bị không tồn tại!"), HttpStatus.NOT_FOUND));
    }



    // Tìm kiếm thiết bị theo tên
    @GetMapping("/search/name")
    public List<Equipment> searchEquipmentsByName(@RequestParam String name) {
        return equipmentService.findEquipmentsByName(name);
    }

}