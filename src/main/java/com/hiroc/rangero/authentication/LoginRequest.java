package com.hiroc.rangero.authentication;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginRequest {
    //email + password
    @Email
    private String email;
    @Size(min=3,max=50)
    private String password;
}
