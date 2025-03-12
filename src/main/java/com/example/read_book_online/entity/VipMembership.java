package com.example.read_book_online.entity;

import com.example.read_book_online.enums.VipStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="vip_membership")
public class VipMembership {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private VipStatusEnum vipStatusEnum;

    public boolean isVipMember() {
        return VipStatusEnum.ACTIVE.equals(vipStatusEnum) && endDate.isAfter(LocalDate.now());
    }
}
