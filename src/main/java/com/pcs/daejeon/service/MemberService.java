package com.pcs.daejeon.service;

import com.pcs.daejeon.common.Util;
import com.pcs.daejeon.dto.member.SignUpDto;
import com.pcs.daejeon.dto.sanction.report.AddReportBullyingDto;
import com.pcs.daejeon.dto.school.SchoolRegistDto;
import com.pcs.daejeon.entity.Member;
import com.pcs.daejeon.entity.NumChkCode;
import com.pcs.daejeon.entity.School;
import com.pcs.daejeon.entity.sanction.ReportBullying;
import com.pcs.daejeon.entity.type.MemberType;
import com.pcs.daejeon.entity.type.RoleTier;
import com.pcs.daejeon.repository.MemberRepository;
import com.pcs.daejeon.repository.NumChkCodeRepository;
import com.pcs.daejeon.repository.SchoolRepository;
import com.pcs.daejeon.repository.sanction.ReportBullyingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberService {

    private final String apiKey = "NCSBR7PKK7XZWLHQ",
            apiSecret = "L6RFT06MA0GG604ZFOZJWYIKWCYSQBRO";

    final DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    private final MemberRepository memberRepository;
    private final NumChkCodeRepository numChkCodeRepository;
    private final SchoolRepository schoolRepository;
    private final ReportBullyingRepository reportBullyingRepository;
    private final Util util;


    public Member saveMember(SignUpDto signUpDto) throws MethodArgumentNotValidException {
        if (memberRepository.validPhoneNumber(signUpDto.getPhoneNumber()) ||
                memberRepository.validLoginId(signUpDto.getLoginId())) {
            throw new IllegalStateException("student already sign up");
        }

        Member member = util.createMember(signUpDto); // password encode

        return memberRepository.save(member);
    }

    public void pushCheckCode(String phoneNumber) {
        Optional<NumChkCode> byPhoneNumber = numChkCodeRepository.findByPhoneNumber(phoneNumber);

        Message message = new Message();

        message.setFrom("01027729778");
        message.setTo(phoneNumber);

        int code = generateUniqueCode();

        if (byPhoneNumber.isEmpty()) {
            numChkCodeRepository.save(new NumChkCode(code, phoneNumber));
        } else {
            byPhoneNumber.get().setCode(code);
        }

        message.setText("[INAB] AnonPost - 가입번호 ["+code+"]");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public void pushAuthCode(String phoneNumber, String code) {
        Optional<NumChkCode> byPhoneNumber = numChkCodeRepository.findByPhoneNumber(phoneNumber);

        Message message = new Message();

        message.setFrom("01027729778");
        message.setTo(phoneNumber);

        if (byPhoneNumber.isEmpty()) {
            throw new IllegalStateException("phone number not found");
        }

        message.setText("[INAB] AnonPost - 인증 코드 ["+code+"]");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public boolean checkCode(int code, String phoneNumber) {
        Optional<NumChkCode> findCode = numChkCodeRepository.findByCodeAndPhoneNumber(code, phoneNumber);

        if (findCode.isEmpty()) {
            return false;
        }

        numChkCodeRepository.delete(findCode.get());
        return true;
    }

    private int generateUniqueCode() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int code = random.nextInt(0, 100001);
        Optional<NumChkCode> byCode = numChkCodeRepository.findByCode(code);

        // 만약 코드가 있다면 새로운 코드 뽑기
        if (byCode.isPresent()) {
            return generateUniqueCode();
        }
        return code;
    }

    public Member saveAdmin(SignUpDto signUpDto, SchoolRegistDto schoolRegistDto, String[] codes) throws Exception {
        School school = new School(schoolRegistDto.getName(),
                schoolRegistDto.getLocate(),
                codes[0], codes[1]);

        if (schoolRepository.valiSchool(school))
            throw new IllegalStateException("school already exist");

        School save = schoolRepository.save(school);

        signUpDto.setSchoolId(save.getId());
        Member member = saveMember(signUpDto);

        member.setRole(RoleTier.ROLE_TIER1);

        return member;
    }

    public void acceptMember(Long memberId) {
        Optional<Member> acceptMember = memberRepository.findById(memberId);
        if (acceptMember.isEmpty()) {
            throw new IllegalStateException("member not found");
        }

        if (isNotSameSchool(acceptMember.get())) {
            throw new IllegalStateException("school is different");
        }

        Member member = acceptMember.get();
        member.setMemberType(MemberType.ACCEPT);

        log.info("[accept-member] accept member: id["+ member.getId() +"]"+ util.getLoginMember().getId());
    }

//    public void rejectMember(Long memberId) {
//        Optional<Member> byId = memberRepository.findById(memberId);
//        if (byId.isEmpty()) {
//            throw new IllegalStateException("member not found");
//        }
//        Member member = byId.get();
//
//        if (isNotSameSchool(member)) {
//            throw new IllegalStateException("school is different");
//        }
//
//        member.setMemberType(MemberType.REJECT);
//        log.info("[reject-member] reject member: id["+ member.getId() +"]"+ util.getLoginMember().getId());
//    }

    public List<Member> getMembers(Long memberId, boolean onlyAdmin) {
        return memberRepository.getMemberList(memberId, onlyAdmin, util.getLoginMember().getSchool());
    }

    public Member setMemberRole(Long memberId, RoleTier tier) {
        Optional<Member> member = memberRepository.findByIdAndSchool(memberId, util.getLoginMember().getSchool());

        if (member.isEmpty()) {
            throw new IllegalStateException("not found member");
        }

        member.get().setRole(tier);

        return member.get();
    }

    public Member findMember(Long id) {
        Optional<Member> byId = memberRepository.findById(id);
        if (byId.isEmpty()) throw new IllegalStateException("member not found");

        return byId.get();
    }

    private boolean isNotSameSchool(Member acceptMember) {
        Member admin = util.getLoginMember();
        return admin.getSchool() != acceptMember.getSchool();
    }

    public void reportBullying(AddReportBullyingDto addReportBullyingDto) {
        Member loginMember = util.getLoginMember();

        ReportBullying reportBullying = new ReportBullying(addReportBullyingDto.getReason(),
                addReportBullyingDto.getPunishmentLevel());

        reportBullyingRepository.save(reportBullying);
    }
}
