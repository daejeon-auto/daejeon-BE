package com.pcs.daejeon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcs.daejeon.entity.basic.BasicEntity;
import com.pcs.daejeon.entity.type.PunishRating;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter @Entity
@NoArgsConstructor
/**
 * 유저 정지(처벌) 삭제 불가
 */
public class Punish extends BasicEntity {

    @Id @Column(name = "punish_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String reason;

    private LocalDateTime expired_date;

    // 신고가 활성화 돼 있는지 - 누적 신고 확인을 위해
    private boolean isValid = true;

    private PunishRating rating;

    @ManyToOne @JsonIgnore
    @JoinColumn(name = "member_id")
    private Member member;

    public Punish(String reason, LocalDateTime expired_date, PunishRating rating, Member member) {
        this.reason = reason;
        this.expired_date = expired_date;
        this.rating = rating;
        this.member = member;
    }

    public void changeValid() {
        isValid = !isValid;
    }

    public void setExpired_date(LocalDateTime expired_date) {
        this.expired_date = expired_date;
    }

    public void setRating(PunishRating rating) {
        this.rating = rating;
    }

}
