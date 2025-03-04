package com.example.read_book_online.config;//package com.example.medical_appointment_booking_app.config;
//
//import com.example.medical_appointment_booking_app.entity.Role;
//import com.example.medical_appointment_booking_app.entity.User;
//import com.example.medical_appointment_booking_app.entity.User.Status;
//import com.example.medical_appointment_booking_app.repository.OrderRepository;
//import com.example.medical_appointment_booking_app.repository.RoleRepository;
//import com.example.medical_appointment_booking_app.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import jakarta.annotation.PostConstruct;
//import java.time.LocalDate;
//
//
//@Configuration
//public class DataInitializer {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    public void init() {
//        initRoles();
//    }
//
//    private void initRoles() {
//        if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
//            Role adminRole = new Role();
//            adminRole.setName("ROLE_ADMIN");
//            roleRepository.save(adminRole);
//        }
//
//        if (!roleRepository.findByName("ROLE_USER").isPresent()) {
//            Role userRole = new Role();
//            userRole.setName("ROLE_USER");
//            roleRepository.save(userRole);
//        }
//
//        initUsers();
//    }
//
//    private void initUsers() {
//        // Lấy roles từ cơ sở dữ liệu
//        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
//                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
//        Role roleUser = roleRepository.findByName("ROLE_USER")
//                .orElseThrow(() -> new RuntimeException("Role USER not found"));
//
//        // Tạo người dùng admin
//        User admin = User.builder()
//                .email("admin@example.com")
//                .password(passwordEncoder.encode("123456"))
//                .role(roleAdmin)
//                .username("admin")
//                .dob(LocalDate.of(1990, 1, 1))
//                .phoneNumber("1234567890")
//                .status(Status.ACTIVE)
//                .setCreatedDate(LocalDate.now())
//                .build();
//
//        userRepository.save(admin);
//
//        // Tạo người dùng thường
//        User user = User.builder()
//                .email("user@example.com")
//                .password(passwordEncoder.encode("123456"))
//                .role(roleUser)
//                .username("user")
//                .dob(LocalDate.of(2000, 1, 1))
//                .phoneNumber("0987654321")
//                .status(Status.ACTIVE)
//                .setCreatedDate(LocalDate.now())
//                .build();
//
//        userRepository.save(user);
//
//    }
//}
//
//
