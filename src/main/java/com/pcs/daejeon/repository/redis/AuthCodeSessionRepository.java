package com.pcs.daejeon.repository.redis;

import com.pcs.daejeon.entity.redis.AuthCodeSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface AuthCodeSessionRepository extends CrudRepository<AuthCodeSession, String> {
    Optional<AuthCodeSession> findByLoginId(String loginId);

}