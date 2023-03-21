package com.pcs.daejeon.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NumChkCode {

    @Id @Column(name = "num_chk_code_id")
    private Long id;

    private Integer code;

    private String phoneNumber;

    public NumChkCode(Integer code, String phoneNumber) {
        this.code = code;
        this.phoneNumber = phoneNumber;
    }
}
