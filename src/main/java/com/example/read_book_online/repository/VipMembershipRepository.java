package com.example.read_book_online.repository;

import com.example.read_book_online.entity.VipMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VipMemberShipRepository extends JpaRepository<VipMembership, Long> {
}
