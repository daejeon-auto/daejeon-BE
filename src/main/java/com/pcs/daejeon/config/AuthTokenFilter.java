package com.pcs.daejeon.config;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authToken = request.getHeader("X-Auth-Token");
        if (authToken != null) {
            // 세션을 찾아서 설정합니다.
            HttpSession session = request.getSession();
            session.setAttribute("SESSION_ID", authToken);
        }
        filterChain.doFilter(request, response);
    }
}
