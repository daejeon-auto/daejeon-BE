package com.pcs.daejeon.dto.sanction.punish;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pcs.daejeon.entity.type.PunishRating;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PunishAddDto {

    @NotNull
    @JsonProperty("member_id")
    private Long memberId;

    @NotEmpty
    private String reason;

    @JsonProperty("expired_date")
    @NotNull
    private LocalDateTime expiredDate;

    @NotNull
    private PunishRating rating;
}
