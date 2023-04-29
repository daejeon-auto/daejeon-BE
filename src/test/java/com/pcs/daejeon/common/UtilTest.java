package com.pcs.daejeon.common;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilTest {

    @Test
    void decrypt() throws Exception {
        // given
        String salt = UUID.randomUUID().toString();
        String encrypted = Util.encrypt("hello world!", salt);

        // when
        String decrypted = Util.decrypt(encrypted, salt);

        // then
        assertEquals("hello world!", decrypted);
    }
}