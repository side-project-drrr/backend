package com.drrr.admin.controller;


import com.drrr.admin.payload.request.AdminSignInRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthenticationApi {

    @PostMapping("/signin")
    public void signIn(@RequestBody @Validated AdminSignInRequest adminSignInRequest) {
        
    }
}
