package com.drrr.auth.payload.dto;

public interface OAuth2Request {
    String findProviderId(String accessToken);

}
