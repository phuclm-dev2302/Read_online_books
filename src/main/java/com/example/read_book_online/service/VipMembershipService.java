package com.example.read_book_online.service;

import com.example.read_book_online.dto.request.RenewalRequest;
import com.example.read_book_online.dto.request.VipMembershipRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.VipMembershipResponse;
import com.example.read_book_online.entity.VipMembership;

public interface VipMembershipService {
    //Đăng kí vip
    ResponseData<VipMembershipResponse> registerVip(VipMembershipRequest vipMembershipRequest);
    // gia hạn vip
    ResponseData<VipMembershipResponse> renewalVip(RenewalRequest renewalRequest);
    // xem thời gian vip
    ResponseData<VipMembershipResponse> getVip();
    // check xem het han chua
    void checkAndUpdateVipStatus(VipMembership vipMembership);

}
