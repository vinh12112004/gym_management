package com.example.ITSS.controller;

import com.example.ITSS.model.Role;
import com.example.ITSS.model.User;
import com.example.ITSS.repository.MemberRepository;
import com.example.ITSS.repository.RoleRepository;
import com.example.ITSS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberRepository memberRepository;
    // Lấy danh sách user
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Cập nhật user (chỉ cập nhật role, không cập nhật password/email/username)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();

        // Lấy role từ DB theo tên, không dùng role mới truyền lên
        Set<Role> newRoles = updatedUser.getRoles().stream()
                .map(r -> roleRepository.findByName(r.getName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + r.getName())))
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}