package com.hiroc.rangero.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
//TODO remove these annotations if not needed
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    //username + email + password + confirm password
    @Size(min=2,max=30)
    private String username;
    @Email
    private String email;

    @Size(min=3,max=50)
    private String password;
    @Size(min=3,max=50)
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
