package com.pcs.daejeon.config.auth;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.type.MemberType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private Member user;

    public PrincipalDetails(Member user) {
        this.user = user;
    }

    // 유저 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().toString();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getMemberType() == MemberType.ACCEPT; // 유저 타입이 ACCEPT가 아닌 사람은 LOCK된 계정으로 인식
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getMemberType() == MemberType.ACCEPT; // 유저 타입이 ACCEPT가 아닌 사람은 LOCK된 계정으로 인식
    }
}
