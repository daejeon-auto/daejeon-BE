package com.pcs.daejeon.config.auth;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정서 login으로 보내줄 때 UserDetailService타입으로 IoC돼있는 loadBtUsername함수 실행
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new IllegalStateException("not found member");
        }
        return new PrincipalDetails(member);
    }
}
