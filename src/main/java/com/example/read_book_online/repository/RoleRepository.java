package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public  interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

}
