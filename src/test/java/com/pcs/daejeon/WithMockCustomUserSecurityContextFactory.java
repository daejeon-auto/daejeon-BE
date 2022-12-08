package com.pcs.daejeon;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    MemberRepository memberRepository;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {

        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        Member member = new Member(
                "test",
                "050323",
                "01027729778",
                "20201",
                "password",
                "loginId",
                AuthType.DIRECT
        );
        member.setRole(RoleTier.ROLE_TIER2);
        memberRepository.save(member);

        final UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(new PrincipalDetails(
                member),
                "password",
                Arrays.asList(new SimpleGrantedAuthority(annotation.role())));

        securityContext.setAuthentication(authenticationToken);
        return securityContext;
    }

}
