package com.pcs.daejeon.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NumChkCode {

    @Id @Column(name = "num_chk_code_id")
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
