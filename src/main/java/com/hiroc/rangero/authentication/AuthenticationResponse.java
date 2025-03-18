package com.hiroc.rangero.authentication;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationResponse {
    private String token; //jwt token
    private String email; //primary key
    private String username; //identifier
    private String profilePictureURL; //TODO when S3 Added
}
