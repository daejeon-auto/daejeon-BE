package com.pcs.daejeon.dto.punish;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pcs.daejeon.entity.type.PunishRating;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PunishUpdateDto {
    @NotNull @JsonProperty("punish_id")
    private Long punishId;

    /**
     * 하루 간격으로 수정 가능
     */
    @NotNull @JsonProperty("update_date")
    private Integer updateDate;

    @NotNull
    private PunishRating rating;
}

