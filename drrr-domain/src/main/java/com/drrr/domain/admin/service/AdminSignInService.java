package com.drrr.domain.admin.service;


import com.drrr.core.exception.admin.AdminExceptionCode;
import com.drrr.domain.admin.entity.Admin;
import com.drrr.domain.admin.repository.AdminRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminSignInService {
    private final AdminRepository adminRepository;

    public Long execute(final AdminSignInDto adminSignInDto) {

        final Admin admin = adminRepository.findByLoginId(adminSignInDto.loginId)
                .orElseThrow(AdminExceptionCode.FAIL_SIGNIN::invoke);

        admin.validateSamePassword(adminSignInDto.password());

        return admin.getId();
    }

    @Builder
    public record AdminSignInDto(String loginId, String password) {

    }


}
