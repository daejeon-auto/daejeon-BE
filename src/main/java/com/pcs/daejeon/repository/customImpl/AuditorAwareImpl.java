package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return Optional.of(member.getMember().getId().toString());
    }
}
