package com.pcs.daejeon.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AccountDto {

    private String id;
    private String password;
}
