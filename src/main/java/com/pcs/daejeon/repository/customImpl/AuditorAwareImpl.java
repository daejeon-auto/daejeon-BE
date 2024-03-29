package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.security.auth.PrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        PrincipalDetails member = null;
        String testMember = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof PrincipalDetails) {
                member = (PrincipalDetails) principal;
            } else {
                return Optional.of(principal.toString());
            }
        }

        if (member == null) {
            return Optional.empty();
        }

        return Optional.of(member.getMember().getId().toString());
    }
}
