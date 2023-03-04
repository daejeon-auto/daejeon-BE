package com.pcs.daejeon.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AuthType {
    DIRECT, INDIRECT;
    @JsonCreator
    public static AuthType from(String s) {
        return AuthType.valueOf(s.toUpperCase());
    }
}
