package com.example.read_book_online.service;

import com.example.read_book_online.dto.request.RenewalRequest;
import com.example.read_book_online.dto.request.VipMembershipRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.VipMembershipResponse;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.entity.VipMembership;

import java.time.LocalDate;

public interface VipMembershipService {
    // tao vipm
    VipMembership createVipmember(User user, int time);
    // check xem het han chua
    void checkAndUpdateVipStatus(VipMembership vipMembership);
    //Đăng kí vip
    ResponseData<VipMembershipResponse> registerVip(int time);
    // gia hạn vip
    ResponseData<VipMembershipResponse> renewalVip(RenewalRequest renewalRequest);
    // xem thời gian vip
    ResponseData<VipMembershipResponse> getVip();



}
