package com.pcs.daejeon.repository.redis;


import com.pcs.daejeon.entity.redis.ChangeablePwd;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface ChangeablePwdRepository extends CrudRepository<ChangeablePwd, String> {
}
