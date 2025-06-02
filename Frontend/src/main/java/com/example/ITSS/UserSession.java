package com.example.ITSS;

public class UserSession {
    private static String token;
    private static String username;
    private static Long id;
    public static void setToken(String t) { token = t; }
    public static String getToken() { return token; }

    public static void setUsername(String u) { username = u; }
    public static String getUsername() { return username; }

    public static Long getId() {
        return id;
    }

    public static void setId(Long id) {
        UserSession.id = id;
    }

    public static void clear() {
        token = null;
        username = null;
    }
}