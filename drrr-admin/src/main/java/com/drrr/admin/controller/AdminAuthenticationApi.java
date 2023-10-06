package com.drrr.admin.controller;


import com.drrr.admin.payload.request.AdminSignInRequest;
import com.drrr.domain.admin.service.AdminSignInService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthenticationApi {
    private final AdminSignInService adminSignInService;

    @PostMapping("/signin")
    public void signIn(@RequestBody @Validated AdminSignInRequest adminSignInRequest, HttpSession httpSession) {
        final Long adminId = adminSignInService.execute(adminSignInRequest.convertServiceDto());
        httpSession.setAttribute("id", adminId);
    }
}