package com.hiroc.rangero.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    @JsonProperty("user_id")
    private long userId;
    private String email;
    private String username;
}
