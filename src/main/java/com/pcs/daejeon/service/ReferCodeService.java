package com.pcs.daejeon.service;

import com.pcs.daejeon.config.auth.PrincipalDetails;
import com.pcs.daejeon.entity.ReferCode;
import com.pcs.daejeon.entity.type.AuthType;
import com.pcs.daejeon.repository.ReferCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReferCodeService {

    private final ReferCodeRepository referCodeRepository;

    public String generateCode() {
        ReferCode referCode = new ReferCode();
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (member.getMember().getAuthType() == AuthType.INDIRECT) {
            throw new IllegalStateException("this account is not signed up with direct");
        }

        if (referCodeRepository.findReferCodeCount(member.getMember()) > 3) {
            throw new IllegalStateException("too many code");
        }
        referCode.generateCode(member.getMember());
        referCodeRepository.save(referCode);

        return referCode.getCode();
    }

    public List<ReferCode> getReferCodeList() {
        PrincipalDetails member = (PrincipalDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        List<ReferCode> codeList = referCodeRepository.findAllByCodeList(member.getMember());

        return codeList;
    }
}
