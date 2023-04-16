package com.pcs.daejeon.service;

import com.pcs.daejeon.dto.punish.PunishAddDto;
import com.pcs.daejeon.dto.punish.PunishUpdateDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.Punish;
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

    public Punish updatePunish(PunishUpdateDto punishUpdateDto) {
        Optional<Punish> findPunish = punishRepository.findById(punishUpdateDto.getPunishId());

        if (findPunish.isEmpty()) throw new IllegalStateException("punish not found");
        if (findPunish.get().getExpired_date().isAfter(LocalDateTime.now()))
            throw new IllegalStateException("it's already expired");

        Punish punish = findPunish.get();

        punish.setRating(punishUpdateDto.getRating());
        LocalDateTime newPunishExpiredDate = punish.getExpired_date().plusDays(punishUpdateDto.getUpdateDate());

        // 업데이트하려는 시간이 너무 늦으면 에러 발생
        if (newPunishExpiredDate.isAfter(LocalDateTime.now())) throw new IllegalStateException("too low expire date");

        punish.setExpired_date(newPunishExpiredDate);

        return punish;
    }

    public List<Punish> getPunish(Member member) {
        Optional<Member> findMember = memberRepository.findById(member.getId());
        if (findMember.isEmpty()) throw new IllegalStateException("member not found");

        return punishRepository.findAllByMember(member);
    }
}
