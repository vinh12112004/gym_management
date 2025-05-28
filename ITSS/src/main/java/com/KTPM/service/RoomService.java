package com.KTPM.service;

import com.KTPM.model.Room;
import com.KTPM.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room roomDetails) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setName(roomDetails.getName());
                    room.setRoomType(roomDetails.getRoomType());
                    room.setAddress(roomDetails.getAddress());
                    room.setOpenTime(roomDetails.getOpenTime());
                    room.setCloseTime(roomDetails.getCloseTime());
                    room.setRoomStatus(roomDetails.getRoomStatus());
                    return roomRepository.save(room);
                })
                .orElse(null);
    }

    public boolean deleteRoom(Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }
}