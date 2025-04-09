package com.hiroc.rangero.email.dto;


import com.hiroc.rangero.email.enums.EmailType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmailRequest {
    @Email
    private String recipient;

    @NotNull
    private String body;

//    @NotNull
//    @Enumerated(EnumType.STRING)
//    private EmailType emailType;
     private String subject;


}
