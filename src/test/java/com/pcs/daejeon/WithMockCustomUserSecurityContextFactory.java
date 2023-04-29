package com.pcs.daejeon;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import com.pcs.daejeon.service.PunishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PunishService punishService;

    @Autowired
    SchoolRepository schoolRepository;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {

        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        School school = new School("부산컴퓨터과학고등학교", "부산광역시","", "",
                "인스타아이디", "패스워드", UUID.randomUUID().toString());
        schoolRepository.save(school);
        School school2 = new School("미림정보여자고등학교", "서울특별시", "", "",
                "인스타아이디2", "패스워드", UUID.randomUUID().toString());
        schoolRepository.save(school2);

        Member member = new Member(
                "01027729778",
                "password",
                "loginId",
                school
        );
        member.setRole(RoleTier.ROLE_TIER2);
        member.setMemberType(MemberType.ACCEPT);
        memberRepository.save(member);

        List<Punish> activePunish = punishService.getActivePunish(member.getId());

        final UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(
                        new PrincipalDetails(member, activePunish),
                "password",
                List.of(new SimpleGrantedAuthority(annotation.role())));

        securityContext.setAuthentication(authenticationToken);
        return securityContext;
    }

}
