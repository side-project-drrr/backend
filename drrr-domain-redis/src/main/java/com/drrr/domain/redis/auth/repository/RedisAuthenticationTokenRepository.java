package com.drrr.domain.auth.repository;

import com.drrr.domain.auth.entity.AuthenticationToken;
import org.springframework.data.repository.CrudRepository;


public interface RedisAuthenticationTokenRepository extends CrudRepository<AuthenticationToken, Long> {
}
