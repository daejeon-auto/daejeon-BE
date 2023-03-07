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
        String sessionId = request.getHeader("sessionId");

        // Validate the session
        if (validSession(request)) {
            filterChain.doFilter(request, response); // Proceed to the next filter in the chain
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid session");
        }
    }

    private boolean validSession(HttpServletRequest request) {
        String sessionId = request.getHeader("sessionid");

        HttpSession session = request.getSession(false);
        if (session != null && session.getId().equals(sessionId) &&
                sessionId != null && sessionId.startsWith("Bearer ")) {
            return true;
        }
        return false;
    }
}