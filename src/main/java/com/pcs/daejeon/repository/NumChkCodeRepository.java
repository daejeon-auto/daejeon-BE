package com.pcs.daejeon.repository;

import com.pcs.daejeon.entity.NumChkCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NumChkCodeRepository extends JpaRepository<NumChkCode, Long> {

    Optional<NumChkCode> findByCode(Integer code);
    Optional<NumChkCode> findByCodeAndPhoneNumber(Integer code, String phoneNumber);
    Optional<NumChkCode> findByPhoneNumber(String phoneNumber);
}
