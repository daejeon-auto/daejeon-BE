package com.pcs.daejeon.repository.redis;

import com.pcs.daejeon.entity.redis.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface RefreshSessionRepository extends CrudRepository<Session, String> {
}