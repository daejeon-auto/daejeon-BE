package com.pcs.daejeon.config.auth;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.PunishRating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class PrincipalDetails implements UserDetails {

    private Member member;
    private List<Punish> activePunish;

    // 유저 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(member.getRole().toString()));
        return auth;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 정지 기록중 활성 상태이며 ACCESS_DENY급 정지일 시 로그인 실패
        for (Punish punish : activePunish) {
            if (punish.getRating().equals(PunishRating.ACCESS_DENY))
                return false;
        }
        
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return member.getMemberType() == MemberType.ACCEPT; // 유저 타입이 ACCEPT가 아닌 사람은 LOCK된 계정으로 인식
    }
}
