package com.pcs.daejeon.config.oauth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomRememberMeServices extends TokenBasedRememberMeServices {

    public CustomRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        String rememberMeHeader = request.getHeader("X-Remember-Me");
        if (rememberMeHeader != null && rememberMeHeader.equals("true")) {
            return super.processAutoLoginCookie(cookieTokens, request, response);
        } else {
            return null;
        }
    }
}

