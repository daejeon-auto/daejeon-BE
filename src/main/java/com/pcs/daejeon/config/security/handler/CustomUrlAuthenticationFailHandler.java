package com.pcs.daejeon.config.security.handler;

import com.pcs.daejeon.dto.security.AccountResDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.repository.MemberRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomUrlAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private RequestCache requestCache = new HttpSessionRequestCache();
    private final MemberRepository memberRepository;

    public CustomUrlAuthenticationFailHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        String errorMsg = null;

        if(exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
            errorMsg = "id or password not exist";
        } else if(exception instanceof DisabledException) {
            errorMsg = "account is disabled";
            String loginId = request.getParameter("loginId");
            Member byLoginId = memberRepository.findByLoginId(loginId);

            if (byLoginId != null) byLoginId.addFailCnt();
        } else if(exception instanceof LockedException) {
            errorMsg = "account is pending";
        }

        AccountResDto jsonResult = AccountResDto.fail(errorMsg);

        if (jsonConverter.canWrite(jsonResult.getClass(), jsonMimeType)) {
            jsonConverter.write(jsonResult, jsonMimeType, new ServletServerHttpResponse(response));
        }
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}
