package com.pcs.daejeon.service.sanction;

import com.pcs.daejeon.dto.sanction.punish.PunishAddDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.sanction.Punish;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.sanction.PunishRepository;
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

    public void addPunish(PunishAddDto punishAddDto) {

        Optional<Member> isValidMember = memberRepository.findById(punishAddDto.getMemberId());

        if (isValidMember.isEmpty()) {
            throw new IllegalStateException("member not found");
        }

        Punish punish = new Punish(
                punishAddDto.getReason(),
                punishAddDto.getExpiredDate(),
                punishAddDto.getRating(),
                isValidMember.get());
        punishRepository.save(punish);
    }

    public List<Punish> getPunish(Member member) {
        Optional<Member> findMember = memberRepository.findById(member.getId());
        if (findMember.isEmpty()) throw new IllegalStateException("member not found");

        List<Punish> punishes = punishRepository.findAllByMember(member);
        punishes.forEach(val -> {
            if (val.getExpired_date().isAfter(LocalDateTime.now()))
                val.changeValid();
        });

        return punishes;
    }

    public List<Punish> getActivePunish(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) throw new IllegalStateException("member not found");

        List<Punish> punish = getPunish(findMember.get());

        LocalDateTime now = LocalDateTime.now();

        List<Punish> activePunish = punish.stream().map(val -> val.isValid() ? val : null).toList();

        return activePunish;
    }
}
