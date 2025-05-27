package com.example.gym.service;

import java.util.*;

public class StatisticsService {
    public Map<String,Object> getOverview() {
        Map<String,Object> m = new HashMap<>();
        m.put("totalUsers", 10);
        m.put("totalPackages", 5);
        m.put("totalRevenue", 2500.0);
        return m;
    }
}