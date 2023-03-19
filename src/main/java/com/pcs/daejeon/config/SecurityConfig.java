package com.pcs.daejeon.config;

import com.pcs.daejeon.config.auth.JwtUserDetailsService;
import com.pcs.daejeon.config.handler.CustomUrlAuthenticationFailHandler;
import com.pcs.daejeon.config.handler.CustomUrlAuthenticationSuccessHandler;
import com.pcs.daejeon.config.oauth.JwtAuthenticationFilter;
import com.pcs.daejeon.config.oauth.JwtConfig;
import com.pcs.daejeon.dto.security.AccountResDto;
import com.pcs.daejeon.repository.MemberRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberRepository memberRepository;
    private final JwtConfig jwtConfig;

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
                .antMatchers("/login", "/sign-up", "/school/list", "/signup-admin").permitAll()
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
                .rememberMe()
                .key(UUID.randomUUID().toString())
                .tokenValiditySeconds(604800)
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

            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;

            AccountResDto jsonResult = AccountResDto.success(null);
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