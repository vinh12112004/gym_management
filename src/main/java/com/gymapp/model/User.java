package com.gymapp.model;

import java.util.Set;
import java.util.stream.Collectors;

public class User {
    private Long id;
    private String email;
    private String username;
    private String password;
    private Set<Role> roles;

    public User() {}
    public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    // Helper for TableView
    public String getRolesString() {
        if (roles == null) return "";
        return roles.stream().map(Role::getName).collect(Collectors.joining(", "));
    }

    public String getFirstRole() {
        if (roles == null || roles.isEmpty()) return null;
        return roles.iterator().next().getName();
    }
}