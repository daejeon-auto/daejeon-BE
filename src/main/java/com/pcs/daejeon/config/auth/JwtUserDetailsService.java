package com.pcs.daejeon.config.auth;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.service.PunishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PunishService punishService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username);

        if (member == null) throw new UsernameNotFoundException("user is not exist");
        List<Punish> punish = punishService.getPunish(member);
        List<Punish> activePunish = punish.stream().map(val -> val.isValid() ? val : null).toList();

        return new PrincipalDetails(member, activePunish);
    }
}
