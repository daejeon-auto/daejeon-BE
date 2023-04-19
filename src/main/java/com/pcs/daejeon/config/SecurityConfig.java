package com.pcs.daejeon.config;

import com.pcs.daejeon.config.auth.JwtUserDetailsService;
import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.config.auth.PrincipalDetailsService;
import com.pcs.daejeon.config.handler.CustomUrlAuthenticationFailHandler;
import com.pcs.daejeon.config.handler.CustomUrlAuthenticationSuccessHandler;
import com.pcs.daejeon.config.oauth.JwtAuthenticationFilter;
import com.pcs.daejeon.config.oauth.JwtConfig;
import com.pcs.daejeon.dto.member.MemberInfoDto;
import com.pcs.daejeon.dto.security.AccountResDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.service.PunishService;
import com.pcs.daejeon.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberRepository memberRepository;
    private final PrincipalDetailsService principalDetailsService;
    private final PunishService punishService;
    private final JwtConfig jwtConfig;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        final byte[] bytes = "test1234test15234test1234test1234test15234test1234".getBytes();

        super.init(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic();

        http
                .cors()
            .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/admin/personal-info/{id}", "/admin/member/set-role/**", "/admin/posts")
                .hasRole("TIER2")
                .antMatchers("/admin/**").hasAnyRole("TIER1", "TIER2") // 해당 권한을 가진 사람만 접근 가능
                .antMatchers("/login", "/sign-up", "/school/list", "/signup-admin", "/posts", "/push-chk-code",
                        "/chk-code", "/school-info/{schoolId}", "/refresh").permitAll()
                .anyRequest().authenticated() // 다른 주소는 모두 허용
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .usernameParameter("loginId")
                .loginProcessingUrl("/login")
                .successHandler(jwtAuthenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
            .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                }))
                .invalidateHttpSession(true)
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().changeSessionId()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(new JwtUserDetailsService(memberRepository))
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    private AuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String token = jwtConfig.createToken(authentication);
            response.addHeader("X-Auth-Token", "Bearer " + token);

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
            School school = member.getSchool();
            // securityUser의 트랜젝션이 끝났기에 punish만 따로 불러옴
            List<Punish> punish = punishService.getPunish(member);
            MemberInfoDto memberInfoDto = new MemberInfoDto(
                    member.getPhoneNumber(),
                    school.getName(),
                    school.getLocate(),
                    member.getAuthType(),
                    punish);

            AccountResDto jsonResult = AccountResDto.success(memberInfoDto);
            if (jsonConverter.canWrite(jsonResult.getClass(), jsonMimeType)) {
                jsonConverter.write(jsonResult, jsonMimeType, new ServletServerHttpResponse(response));
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomUrlAuthenticationSuccessHandler();
    }
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomUrlAuthenticationFailHandler();
    }
}