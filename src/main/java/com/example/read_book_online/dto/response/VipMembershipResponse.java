package com.example.read_book_online.dto.response;

import com.example.read_book_online.entity.VipMembership;
import com.example.read_book_online.enums.VipStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class VipMembershipResponse {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private VipStatusEnum vipStatusEnum;

    public static VipMembershipResponse from(VipMembership vipMembership) {
        return VipMembershipResponse.builder()
                .userId(vipMembership.getUser().getUserId())
                .startDate(vipMembership.getStartDate())
                .endDate(vipMembership.getEndDate())
                .vipStatusEnum(vipMembership.getVipStatusEnum())
                .build();
    }
}
