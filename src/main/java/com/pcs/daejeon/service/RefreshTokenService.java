package com.pcs.daejeon.service;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.config.oauth.JwtConfig;
import com.pcs.daejeon.entity.Session;
import com.pcs.daejeon.repository.RefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class RefreshTokenService {
    
    private final JwtConfig jwtConfig;
    private final RefreshSessionRepository refreshSessionRepository;
    private final UserDetailsService userDetailsService;
    
    public void setRefreshToken(String token) {
        long validityInMilliseconds = 1000L * 60 * 60 * 24 * 30;
        Date validity = new Date(new Date().getTime() + validityInMilliseconds);

        Session session = new Session(token, validity);
        refreshSessionRepository.save(session);
    }

    /**
     * expire date(session)과 token, loginId가 모두 사용 가능할 시 새 accessToken 발급
     */
    public String createRefreshToken(HttpServletRequest request) {
        String token = jwtConfig.resolveRefreshToken(request);

        // 토큰 유효성 검사
        if (token == null || !jwtConfig.validateToken(token)) return null;

        Optional<Session> session = refreshSessionRepository.findById(token);
        if (session.isEmpty()) return null;

        Date expiredDate = session.get().getExpiredDate();

        // expire 된 토큰이면 삭제후 null return
        if (expiredDate == null || validRefreshToken(expiredDate)) {
            refreshSessionRepository.deleteById(token);
            return null;
        }

        PrincipalDetails principal = (PrincipalDetails) jwtConfig.getAuthentication(token).getPrincipal();

        UserDetails user = userDetailsService.loadUserByUsername(principal.getMember().getLoginId());

        return user != null ? jwtConfig.createToken(jwtConfig.getAuthentication(token)) : null;
    }

    private boolean validRefreshToken(Date expiredDate) {
        // if expireDate < now return true
        boolean after = expiredDate.before(new Date());

        return after;
    }
}
