package com.pcs.daejeon.dto.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class AccountResDto<T> {
    private T data;
    private boolean hasError;

    public static AccountResDto success(Object data) {
        return new AccountResDto(data, false);
    }

    public static AccountResDto fail(Object data) {
        return new AccountResDto(data, true);
    }

    private AccountResDto(T data, boolean hasError) {
        this.data = data;
        this.hasError = hasError;
    }
}
