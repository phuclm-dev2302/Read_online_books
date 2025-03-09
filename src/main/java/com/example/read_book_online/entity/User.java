package com.example.read_book_online.entity;

import com.example.read_book_online.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Address> addresses;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Role_ID", nullable = false)
    private Role role;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private LocalDate dob;

    private String username;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private String otp;

    private LocalDate setCreatedDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BookInteraction> interactions; // Danh sách sách đã like hoặc xem

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getName() == null ? List.of() : List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return status == StatusEnum.ACTIVE;
    }
}
