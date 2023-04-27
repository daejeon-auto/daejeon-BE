package com.pcs.daejeon.config.oauth;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtConfig {

    private static final String key = "inab@anonpost.secretKey";

//    @PostConstruct
//    private void JwtConfig() {
//        KeyGenerator keyGen = null;
//        try {
//            keyGen = KeyGenerator.getInstance("AES");
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//        keyGen.init(256); // 키 길이 설정
//        SecretKey secretKey = keyGen.generateKey();
//        key = Arrays.toString(secretKey.getEncoded());
//        byte[] encodedKey = secretKey.getEncoded();
//    }

    private final UserDetailsService userDetailsService;

    public String createToken(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Date now = new Date();
        // 3H
        long validityInMilliseconds = 1000L * 60 * 60 * 3;
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(principal.getMember().getLoginId())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Date now = new Date();

        // 1M
        long validityInMilliseconds = 1000L * 60 * 60 * 24 * 30;
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(principal.getMember().getLoginId())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("X-Auth-Token");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * refresh JWT 토큰 디코딩
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("X-Refresh-Token");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            if (e.getClass() == ExpiredJwtException.class) return false;

            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        Claims body = claimsJws
                .getBody();
        String id = body.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}