package com.example.read_book_online.entity;

import com.example.read_book_online.enums.VipStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="vip_membership")
public class VipMembership {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String orderId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private VipStatusEnum vipStatusEnum;

    public boolean isVipMember() {
        return VipStatusEnum.ACTIVE.equals(vipStatusEnum) && (endDate != null && endDate.isAfter(LocalDate.now()));
    }
}
