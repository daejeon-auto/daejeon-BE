package com.pcs.daejeon.dto.school;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class MealApiDto {
    @JsonProperty("ATPT_OFCDC_SC_CODE") // 시도교육청 코드
    private String provinceOfficeCode;

    @JsonProperty("ATPT_OFCDC_SC_NM") // 시도교육청명
    private String provinceOfficeName;

    @JsonProperty("SD_SCHUL_CODE") // 표준학교코드
    private String schoolCode;

    @JsonProperty("SCHUL_NM") // 학교명
    private String schoolName;

    @JsonProperty("MMEAL_SC_CODE") // 식사코드
    private String mealCode;

    @JsonProperty("MMEAL_SC_NM") // 식사명
    private String mealName;

    @JsonProperty("MLSV_YMD") // 급식일자
    private String feedingDate;

    @JsonProperty("MLSV_FGR") // 급식인원수
    private int numServed;

    @JsonProperty("DDISH_NM") // 요리명
    private String dishName;

    @JsonProperty("ORPLC_INFO") // 원산지 정보
    private String originInfo;

    @JsonProperty("CAL_INFO") // 칼로리 정보
    private String calorieInfo;

    @JsonProperty("NTR_INFO") // 영양 정보
    private String nutritionalInfo;

    @JsonProperty("MLSV_FROM_YMD") // 급식 시작일자
    private String feedStartDate;

    @JsonProperty("MLSV_TO_YMD") // 급식 종료일자
    private String feedEndDate;

}