package com.example.read_book_online.service.impl;
import com.example.read_book_online.config.exception.UserAlreadyVipException;
import com.example.read_book_online.config.exception.UserNotRegisteredVip;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.VipMembershipResponse;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.entity.VipMembership;
import com.example.read_book_online.enums.VipStatusEnum;
import com.example.read_book_online.repository.VipMembershipRepository;
import com.example.read_book_online.service.UserService;
import com.example.read_book_online.service.VipMembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VipMembershipServiceImpl implements VipMembershipService {
    private final UserService userService;
    private final VipMembershipRepository vipMembershipRepository;

    @Override
    public ResponseData<VipMembershipResponse> registerVip(int time ) {
        User user = userService.getUserBySecurity();

        // Kiểm tra xem user đã có gói VIP chưa
        Optional<VipMembership> existingVip = vipMembershipRepository.findByUserUserId(user.getUserId());
        if (existingVip.isPresent()) {
            throw new UserAlreadyVipException("User with ID " + user.getUserId() + " is already a VIP member.");
        }

        // Nếu chưa có, tạo mới VIP Membership
        VipMembership vipm = createVipmember(user, time);
        return new ResponseData<>(200, "Register VipMembership successfully", VipMembershipResponse.from(vipm));
    }

    @Override
    public ResponseData<VipMembershipResponse> renewalVip(int time) {
        User user = userService.getUserBySecurity();
        VipMembership vipMembership = vipMembershipRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new UserNotRegisteredVip("User not registered VIP!, please register VIP first."));

        vipMembership.setEndDate(vipMembership.getEndDate().plusMonths(time));

        vipMembershipRepository.save(vipMembership);

        return new ResponseData<>(200, "Renewal VipMembership successfully", VipMembershipResponse.from(vipMembership));
    }

    @Override
    public ResponseData<VipMembershipResponse> getVip() {
        User user = userService.getUserBySecurity();
        VipMembership vipMembership = vipMembershipRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new UserNotRegisteredVip("User not registered VIP!"));

        // Kiểm tra và cập nhật trạng thái VIP
        checkAndUpdateVipStatus(vipMembership);

        return new ResponseData<>(200, "Get VipMembership successfully", VipMembershipResponse.from(vipMembership));
    }

    @Override
    public void checkAndUpdateVipStatus(VipMembership vipMembership) {
        if (vipMembership.getEndDate().isBefore(LocalDate.now())
                && vipMembership.getVipStatusEnum() == VipStatusEnum.ACTIVE) {
            vipMembership.setVipStatusEnum(VipStatusEnum.EXPIRED);
            vipMembershipRepository.save(vipMembership);
        }
    }

    @Override
    public VipMembership createVipmember(User user,int time){
        VipMembership vipMembership = VipMembership.builder()
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(time))
                .vipStatusEnum(VipStatusEnum.ACTIVE)
                .build();
        vipMembershipRepository.save(vipMembership);
        return vipMembership;
    }

}
