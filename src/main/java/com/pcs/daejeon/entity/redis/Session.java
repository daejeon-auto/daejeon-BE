package com.pcs.daejeon.entity.redis;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@Data
@AllArgsConstructor
@RedisHash(value = "refresh_session",
        timeToLive = 60 * 60 * 24 * 30) // 1달
public class Session {

    @Id
    private String session;
    private Date expiredDate;
}
