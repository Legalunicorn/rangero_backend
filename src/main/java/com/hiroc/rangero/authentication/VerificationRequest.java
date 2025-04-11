package com.hiroc.rangero.authentication;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VerificationRequest {
    @Email
    private String email;
    private int otp;
}
