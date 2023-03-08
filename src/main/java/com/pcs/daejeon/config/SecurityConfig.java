package com.pcs.daejeon.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.pcs.daejeon.config.auth.JwtUserDetailsService;
import com.pcs.daejeon.config.handler.CustomUrlAuthenticationFailHandler;
import com.pcs.daejeon.config.handler.CustomUrlAuthenticationSuccessHandler;
import com.pcs.daejeon.config.oauth.DefaultAuthenticationSuccessHandler;
import com.pcs.daejeon.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberRepository memberRepository;

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private SecretKeySpec jwtSecretKeySpec;

    @Override
    public void init(WebSecurity web) throws Exception {
        final byte[] bytes = "test1234test15234test1234test1234test15234test1234".getBytes();
        jwtSecretKeySpec = new SecretKeySpec(bytes, 0, bytes.length, "AES");

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
                .antMatchers("/login", "/sign-up", "/school/list", "/signup-admin").permitAll()
                .anyRequest().authenticated() // 다른 주소는 모두 허용
            .and()
                .formLogin()
                .usernameParameter("loginId")
                .loginProcessingUrl("/login")
                .successHandler(
                    new DefaultAuthenticationSuccessHandler(new NimbusJwtEncoder(
                            new ImmutableSecret<>(jwtSecretKeySpec)
                    )))
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