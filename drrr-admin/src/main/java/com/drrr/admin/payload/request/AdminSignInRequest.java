package com.drrr.admin.payload.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NonNull;


@Builder
public record AdminSignInRequest(
        @NonNull @Size(min = 8, max = 12) String loginId,
        @NonNull @Size(min = 8, max = 20) String password
) {
}
