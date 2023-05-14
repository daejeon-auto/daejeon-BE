package com.pcs.daejeon.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@RedisHash(value = "changeablePwd", timeToLive = 60 * 10) // 만료시간 10분
public class ChangeablePwd {

    @Id
    private String phoneNumber;

    private String accountId;
}
