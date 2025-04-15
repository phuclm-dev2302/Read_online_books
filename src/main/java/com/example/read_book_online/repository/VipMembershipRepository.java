package com.example.read_book_online.repository;

import com.example.read_book_online.entity.VipMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VipMembershipRepository extends JpaRepository<VipMembership, Long> {

    Optional<VipMembership> findByUserUserId(Long userId);
    Optional<VipMembership> findByOrderId(String orderId);
}