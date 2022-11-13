package com.pcs.daejeon.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcs.daejeon.config.tocken.AjaxAuthenticationToken;
import com.pcs.daejeon.dto.AccountDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessFilter extends AbstractAuthenticationProcessingFilter {
    //JSON방식으로 데이터를 담아 요청을 하기에 해당 Json을 객체에 담기 위해 ObjectMapper를 사용한다
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessFilter() {
        super(new AntPathRequestMatcher("/login"));
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if(!isAjax(request)){
            throw new IllegalStateException("Authentication is not supported");
        }
        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);
        if(StringUtils.isEmpty(accountDto.getId())||StringUtils.isEmpty(accountDto.getPassword())){
            throw new IllegalStateException("login id or Password is empty");
        }
        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(accountDto.getId(), accountDto.getPassword());

        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
