package com.example.ITSS.controller;

import com.example.ITSS.model.Equipment;
import com.example.ITSS.model.Room;
import com.example.ITSS.service.EquipmentService;
import com.example.ITSS.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipments")
@CrossOrigin("*")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final RoomService roomService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService,
                               RoomService roomService) {
        this.equipmentService = equipmentService;
        this.roomService = roomService;
    }

    /**
     * Lấy toàn bộ thiết bị
     */
    @GetMapping
    public List<Equipment> getAllEquipments() {
        return equipmentService.getAllEquipments();
    }

    /**
     * Lấy thiết bị theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Tạo mới thiết bị.
     * Payload phải chứa nested room với đúng id:
     * {
     *   "name": "...",
     *   "type": "...",
     *   "status": "...",
     *   "purchaseDate": "2025-06-11T10:00:00",
     *   "price": 123.45,
     *   "description": "...",
     *   "room": { "id": 5 }
     * }
     */
    @PostMapping
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        // Kiểm tra phòng tồn tại
        if (equipment.getRoom() == null || equipment.getRoom().getId() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        Optional<Room> roomOpt = roomService.getRoomById(equipment.getRoom().getId());
        if (roomOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        equipment.setRoom(roomOpt.get());
        Equipment saved = equipmentService.saveEquipment(equipment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Cập nhật thiết bị, payload cũng cần chứa đúng room.id
     */
    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable Long id,
            @RequestBody Equipment equipment) {

        return equipmentService.getEquipmentById(id)
                .map(existing -> {
                    // Kiểm tra và gán Room
                    if (equipment.getRoom() == null || equipment.getRoom().getId() == null) {
                        throw new IllegalArgumentException("Missing room.id");
                    }
                    Room room = roomService.getRoomById(equipment.getRoom().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Room not found"));
                    equipment.setId(id);
                    equipment.setRoom(room);

                    Equipment updated = equipmentService.saveEquipment(equipment);
                    return new ResponseEntity<>(updated, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Xóa thiết bị theo ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEquipment(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id)
                .map(e -> {
                    equipmentService.deleteEquipment(id);
                    return new ResponseEntity<>(Map.of("message", "Xóa thiết bị thành công!"), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(Map.of("message", "Thiết bị không tồn tại!"), HttpStatus.NOT_FOUND));
    }
}
