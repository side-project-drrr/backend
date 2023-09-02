package com.example.drrrapi.auth.infrastructure.redis;

import com.example.drrrapi.auth.entity.AuthenticationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface RedisAuthenticationTokenRepository extends CrudRepository<AuthenticationToken, Long> {
}
