package com.pcs.daejeon.config.security.handler;

import com.pcs.daejeon.config.security.auth.PrincipalDetails;
import com.pcs.daejeon.config.security.oauth.JwtConfig;
import com.pcs.daejeon.dto.member.MemberInfoDto;
import com.pcs.daejeon.dto.security.AccountResDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.sanction.Punish;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.service.RefreshTokenService;
import com.pcs.daejeon.service.sanction.PunishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CustomUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final PunishService punishService;
    private final JwtConfig jwtConfig;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String token = jwtConfig.createToken(authentication);
        response.addHeader("X-Auth-Token", "Bearer " + token);

        // 만약 remember가 true일 때 X-Refresh-Token 발급
        if (Objects.equals(request.getParameter("rememberMe"), "true")) {
            String refreshToken = jwtConfig.createRefreshToken(authentication);
            response.addHeader("X-Refresh-Token", "Bearer " + refreshToken);

            // 검증을 위해 저장
            refreshTokenService.setRefreshToken(refreshToken);
        }

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        PrincipalDetails securityUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof UserDetails) {
                securityUser = (PrincipalDetails) principal;
            }
        }

        Member member = securityUser.getMember();
        memberRepository.findById(member.getId()).get().resetFailCnt();
        School school = member.getSchool();
        // securityUser의 트랜젝션이 끝났기에 punish만 따로 불러옴
        List<Punish> punish = punishService.getPunish(member);
        MemberInfoDto memberInfoDto = new MemberInfoDto(
                member.getPhoneNumber(),
                school.getName(),
                school.getLocate(),
                punish);

        AccountResDto jsonResult = AccountResDto.success(memberInfoDto);
        if (jsonConverter.canWrite(jsonResult.getClass(), jsonMimeType)) {
            jsonConverter.write(jsonResult, jsonMimeType, new ServletServerHttpResponse(response));
        }
    }
}
