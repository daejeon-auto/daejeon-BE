package com.pcs.daejeon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstagramResponseDto {

    private String status;
    private String msg;
    @JsonProperty("direct_link")
    private String url;
}
