package com.drrr.alarm.service.request;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Builder
public record SubscriptionRequest (
        Long id,
        String endpoint,
        String expirationTime,
        String p256dh, // public key
        String auth
){
}
