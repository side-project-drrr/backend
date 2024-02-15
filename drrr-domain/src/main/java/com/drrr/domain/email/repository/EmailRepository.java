package com.drrr.domain.email.repository;

import com.drrr.domain.email.entity.Email;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findByProviderId(final String providerId);

    void deleteByProviderId(final String providerId);

    boolean existsByProviderId(final String providerId);

}
