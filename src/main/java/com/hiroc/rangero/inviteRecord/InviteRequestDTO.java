package com.hiroc.rangero.inviteRecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InviteRequestDTO {
    @Email
    private String email;
    @JsonProperty("project_id")
    private long projectId;
}
