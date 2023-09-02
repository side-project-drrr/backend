package com.drrr.auth.infrastructure.redis;

import com.drrr.auth.entity.AuthenticationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface RedisAuthenticationTokenRepository extends CrudRepository<AuthenticationToken, Long> {
}
