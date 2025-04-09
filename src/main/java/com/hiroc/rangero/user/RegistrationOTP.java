package com.hiroc.rangero.user;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Builder
@AllArgsConstructor
@Getter
@Setter
public class RegistrationOTP {
    private int otpCode;
    private LocalDateTime expireTime;
}
