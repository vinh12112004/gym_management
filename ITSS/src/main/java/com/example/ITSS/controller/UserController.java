package com.example.ITSS.controller;

import com.example.ITSS.model.Role;
import com.example.ITSS.model.User;
import com.example.ITSS.repository.MemberRepository;
import com.example.ITSS.repository.RoleRepository;
import com.example.ITSS.repository.StaffRepository;
import com.example.ITSS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (roleNames.contains("MANAGER") || roleNames.contains("TRAINER")) {
            staffRepository.deleteByEmail(user.getEmail());
        } else if (roleNames.contains("MEMBER")) {
            memberRepository.deleteByEmail(user.getEmail());
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
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        user.setRoles(newRoles);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();

        // Lấy role
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        // Tạo response chứa thông tin user + họ tên nếu có
        String firstName = null;
        String lastName = null;
        if (roles.contains("MEMBER")) {
            var member = memberRepository.findByEmail(email);
            if (member.isPresent()) {
                firstName = member.get().getFirstName();
                lastName = member.get().getLastName();
            }
        } else if (roles.contains("MANAGER") || roles.contains("TRAINER") || roles.contains("STAFF")) {
            var staff = staffRepository.findByEmail(email);
            if (staff.isPresent()) {
                firstName = staff.get().getFirstName();
                lastName = staff.get().getLastName();
            }
        }

        // Tạo response custom
        var response = new java.util.HashMap<String, Object>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("username", user.getUsername());
        response.put("roles", roles);
        response.put("firstName", firstName);
        response.put("lastName", lastName);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/email/{email}")
    public ResponseEntity<User> updateUserByEmail(@PathVariable String email, @RequestBody User updatedUser) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();

        // Cập nhật password nếu có
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Cập nhật roles nếu có
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            Set<Role> newRoles = updatedUser.getRoles().stream()
                    .map(r -> roleRepository.findByName(r.getName())
                            .orElseThrow(() -> new RuntimeException("Role not found: " + r.getName())))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}