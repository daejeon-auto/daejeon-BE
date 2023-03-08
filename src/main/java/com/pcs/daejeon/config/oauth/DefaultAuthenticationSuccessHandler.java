package com.pcs.daejeon.config.oauth;

import com.pcs.daejeon.dto.security.AccountResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtEncoder jwtEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String auth = authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .claim("authorities", auth)
                .expiresAt(LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.UTC))
                .issuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .build();

        Jwt jwt = jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256)
                        .build(), claimsSet
                ));

        String tokenValue = jwt.getTokenValue();
        log.debug("tokenValue: {}", tokenValue);

        response.addHeader(HttpHeaders.AUTHORIZATION, tokenValue);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        AccountResDto jsonResult = AccountResDto.success(null);
        if (jsonConverter.canWrite(jsonResult.getClass(), jsonMimeType)) {
            jsonConverter.write(jsonResult, jsonMimeType, new ServletServerHttpResponse(response));
        }
    }
}
