package com.pcs.daejeon.common;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Util {

    private final PasswordEncoder pwdEncoder;
    private final SchoolRepository schoolRepository;

    public Member createMember(SignUpDto signUpDto) {
        Optional<School> school = schoolRepository.findById(signUpDto.getSchoolId());

        if (school.isEmpty()) {
            throw new IllegalStateException("school not found");
        }

        return new Member(
                signUpDto.getPhoneNumber(),
                pwdEncoder.encode(signUpDto.getPassword()),
                signUpDto.getLoginId(),
                school.get());
    }

    public Member getLoginMember() {
        try {
            PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            return member.getMember();
        } catch (Exception e) {
            return null;
        }
    }

    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "inab@anonpost.secretkey.forencrypt.instagramInfo".getBytes();

    public static String encrypt(String valueToEnc, String salt) throws Exception {
        String saltedVal = valueToEnc + salt;
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(saltedVal.getBytes());
        return Base64.getEncoder().encodeToString(encValue);
    }

    public static String decrypt(String encryptedValue, String salt) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);
        String decStr = new String(decValue);
        return decStr.substring(0, decStr.length() - salt.length());
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
}

