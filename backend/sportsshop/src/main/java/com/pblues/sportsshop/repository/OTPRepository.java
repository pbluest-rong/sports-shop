package com.pblues.sportsshop.repository;

import com.pblues.sportsshop.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    void deleteByEmail(String email);

    @Query("SELECT ct FROM OTP ct WHERE ct.email = :email")
    Optional<OTP> findByEmail(String email);
}
