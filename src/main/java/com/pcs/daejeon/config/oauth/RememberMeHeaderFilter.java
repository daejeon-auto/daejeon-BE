package com.pcs.daejeon.config.oauth;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * rememberMe 의 쿠키로 나가는 토큰 값을 헤더에 담아줌
 */
public class RememberMeHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletResponse wrappedResponse = new HttpServletResponseWrapper(response) {
            @Override
            public void addCookie(Cookie cookie) {
                if (cookie.getName().equals("remember-me")) {
                    this.addHeader("X-Remember-Me", cookie.getValue());
                } else {
                    super.addCookie(cookie);
                }
            }
        };
        filterChain.doFilter(request, wrappedResponse);
    }
}
