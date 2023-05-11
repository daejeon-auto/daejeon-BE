package com.pcs.daejeon.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@Data
@AllArgsConstructor
@RedisHash(value = "refresh-session", timeToLive = -1L)
public class Session {

    @Id
    private String session;
    private Date expiredDate;
}
