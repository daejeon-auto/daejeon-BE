package com.pcs.daejeon.service;

import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
import com.pcs.daejeon.entity.type.PunishRating;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.PunishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @Log4j2
@RequiredArgsConstructor
@Transactional
public class PunishService {

    private final PunishRepository punishRepository;
    private final MemberRepository memberRepository;

    public void addPunish(Member member, String reason, LocalDateTime expireDate, PunishRating punishRating) {

        Optional<Member> isValidMember = memberRepository.findById(member.getId());

        if (isValidMember.isEmpty()) {
            throw new IllegalStateException("member not found");
        }

        Punish punish = new Punish(
                reason,
                expireDate,
                punishRating,
                member
        );
        punishRepository.save(punish);
    }

    public List<Punish> getPunish(Member member) {
        Optional<Member> findMember = memberRepository.findById(member.getId());
        if (findMember.isEmpty()) throw new IllegalStateException("member not found");

        return punishRepository.findAllByMember(member);
    }
}
