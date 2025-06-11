package com.gymapp.model;

public class Room {
    private Long id;
    private String name;
    private String roomType;    // Gym, Yoga, Fitness
    private String address;
    private String openTime;    // “HH:mm” format
    private String closeTime;   // “HH:mm” format
    private String roomStatus;

    /** Số lượng thiết bị gắn với phòng này */
    private int equipmentCount;

    public Room() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRoomType() {
        return roomType;
    }
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenTime() {
        return openTime;
    }
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getRoomStatus() {
        return roomStatus;
    }
    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public int getEquipmentCount() {
        return equipmentCount;
    }
    public void setEquipmentCount(int equipmentCount) {
        this.equipmentCount = equipmentCount;
    }
}
