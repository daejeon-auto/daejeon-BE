package com.pcs.daejeon.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NumChkCode {

    @Id @Column(name = "chk_code_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer code;

    private String phoneNumber;

    public void setCode(Integer code) {
        this.code = code;
    }

    public NumChkCode(Integer code, String phoneNumber) {
        this.code = code;
        this.phoneNumber = phoneNumber;
    }
}
