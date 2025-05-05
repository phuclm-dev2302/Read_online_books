package com.example.read_book_online.service.impl;
import com.example.read_book_online.config.exception.MomoException;
import com.example.read_book_online.config.exception.UserAlreadyVipException;
import com.example.read_book_online.config.exception.UserNotRegisteredVip;
import com.example.read_book_online.controller.PaymentController;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.VipMembershipResponse;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.entity.VipMembership;
import com.example.read_book_online.enums.VipStatusEnum;
import com.example.read_book_online.repository.VipMembershipRepository;
import com.example.read_book_online.service.MomoPaymentService;
import com.example.read_book_online.service.UserService;
import com.example.read_book_online.service.VipMembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VipMembershipServiceImpl implements VipMembershipService {
    private final UserService userService;
    private final VipMembershipRepository vipMembershipRepository;
    private final MomoPaymentService momoPaymentService;

    @Override
    public ResponseData<String> registerVip(int time, String amount) {
        User user = userService.getUserBySecurity();
        String paymentResult = null;

        Optional<VipMembership> existingVipOpt = vipMembershipRepository.findByUserUserId(user.getUserId());

        if (existingVipOpt.isPresent()) {
            VipMembership existingVip = existingVipOpt.get();
            if (existingVip.isVipMember()) {
                throw new UserAlreadyVipException("User with ID " + user.getUserId() + " is already a VIP member.");
            }

            // Nếu đã có VipMembership nhưng chưa phải VIP, có thể cập nhật lại
            paymentResult = momoPaymentService.createPaymentRequest(amount);
            log.info("MoMo payment request created with amount: {}", amount);

            existingVip.setVipStatusEnum(VipStatusEnum.EXPIRED);
            vipMembershipRepository.save(existingVip);

        } else {
            // Chưa có VipMembership -> tạo mới
            paymentResult = momoPaymentService.createPaymentRequest(amount);
            log.info("MoMo payment request created with amount: {}", amount);

            createVipmember(user, time);
        }

        return new ResponseData<>(200, "Please make payment using the URL", paymentResult);
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
