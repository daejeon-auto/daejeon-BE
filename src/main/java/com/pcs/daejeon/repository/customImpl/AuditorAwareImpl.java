package com.pcs.daejeon.repository.customImpl;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        PrincipalDetails member = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof UserDetails) {
                member = (PrincipalDetails) principal;
            }
        }

        if (member == null) {
            return Optional.empty();
        }

        return Optional.of(member.getMember().getId().toString());
    }
}
