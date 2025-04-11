package com.hiroc.rangero.user;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationOTP {
    private int otpCode;
    private LocalDateTime expireTime;
}
