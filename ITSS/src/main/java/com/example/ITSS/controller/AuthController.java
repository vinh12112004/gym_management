package com.example.ITSS.controller;

import com.example.ITSS.model.Member;
import com.example.ITSS.model.Role;
import com.example.ITSS.model.Staff;
import com.example.ITSS.model.User;
import com.example.ITSS.repository.MemberRepository;
import com.example.ITSS.repository.RoleRepository;
import com.example.ITSS.repository.StaffRepository;
import com.example.ITSS.repository.UserRepository;
import com.example.ITSS.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.StreamSupport;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private com.example.ITSS.security.CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {
        try {
            String usernameOrEmail = user.get("username");
            String password = user.get("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
            String token = jwtUtil.generateToken(userDetails.getUsername());

            User dbUser = userRepository.findByUsername(usernameOrEmail)
                    .or(() -> userRepository.findByEmail(usernameOrEmail))
                    .orElse(null);
            Long id = dbUser != null ? dbUser.getId() : null;
            String username = dbUser != null ? dbUser.getUsername() : "";
            Set<String> roles = dbUser != null
                    ? dbUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                    : Set.of();

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "id", id,
                    "username", username,
                    "roles", roles
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, Object> req) {
        String username = (String) req.get("username");
        String email = (String) req.get("email");
        String password = (String) req.get("password");
        String firstName = (String) req.get("firstName");
        String lastName = (String) req.get("lastName");
        String phone = (String) req.get("phone");

        Set<Role> roles = null;
        Object rolesObj = req.get("roles");
        if (rolesObj instanceof Iterable<?>) {
            roles = StreamSupport.stream(((Iterable<?>) rolesObj).spliterator(), false)
                    .map(r -> roleRepository.findByName(String.valueOf(r))
                            .orElseGet(() -> roleRepository.save(new Role(null, String.valueOf(r)))))
                    .collect(Collectors.toSet());
        }
        if (roles == null || roles.isEmpty()) {
            Role roleOwner = roleRepository.findByName("OWNER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "OWNER")));
            roles = Set.of(roleOwner);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        userRepository.save(user);

        boolean isMember = roles.stream().anyMatch(r -> "MEMBER".equalsIgnoreCase(r.getName()));
        if (isMember) {
            Member member = new Member();
            member.setFirstName(firstName);
            member.setLastName(lastName);
            member.setEmail(email);
            member.setPhone(phone);
            member.setJoinDate(java.time.LocalDate.now());
            member.setStatus("ACTIVE");
            member.setMembershipType("STANDARD");
            memberRepository.save(member);
        }

        // Tạo staff nếu là MANAGER hoặc TRAINER
        boolean isManager = roles.stream().anyMatch(r -> "MANAGER".equalsIgnoreCase(r.getName()));
        boolean isTrainer = roles.stream().anyMatch(r -> "TRAINER".equalsIgnoreCase(r.getName()));
        if (isManager || isTrainer) {
            Staff staff = new Staff();
            staff.setFirstName(firstName);
            staff.setLastName(lastName);
            staff.setEmail(email);
            staff.setPhone(phone);
            staff.setPosition(isManager ? "MANAGER" : "TRAINER");
            staffRepository.save(staff);
        }

        return Map.of("message", "User registered successfully");
    }
}