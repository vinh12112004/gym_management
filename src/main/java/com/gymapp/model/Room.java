package com.gymapp.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Room {
    private Long id;
    private String name;
    private String roomType;    // Will receive enum as string from backend
    private String address;
    
    @JsonDeserialize(using = TimeArrayDeserializer.class)
    private String openTime;    // Convert from LocalTime array to "HH:mm" format
    
    @JsonDeserialize(using = TimeArrayDeserializer.class)
    private String closeTime;   // Convert from LocalTime array to "HH:mm" format
    
    private String roomStatus;  // Will receive enum as string from backend
    private int equipmentCount; // Formula field from backend

    public Room() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }

    public String getRoomStatus() { return roomStatus; }
    public void setRoomStatus(String roomStatus) { this.roomStatus = roomStatus; }

    public int getEquipmentCount() { return equipmentCount; }
    public void setEquipmentCount(int equipmentCount) { this.equipmentCount = equipmentCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id != null && id.equals(room.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name; // Để ComboBox hiển thị tên phòng
    }
    
    // Enum constants for validation/display purposes
    public static class RoomType {
        public static final String GYM = "Gym";
        public static final String YOGA = "Yoga"; 
        public static final String FITNESS = "Fitness";
    }
    
    public static class RoomStatus {
        public static final String AVAILABLE = "Available";
        public static final String OCCUPIED = "Occupied";
        public static final String MAINTENANCE = "Maintenance";
        public static final String CLOSED = "Closed";
    }
}