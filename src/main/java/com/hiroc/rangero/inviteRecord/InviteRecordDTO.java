package com.hiroc.rangero.inviteRecord;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.assertj.core.api.recursive.assertion.RecursiveAssertionConfiguration;

@Data
@Builder
@AllArgsConstructor
public class InviteRecordDTO {
    private String inviteeEmail;
    private Long projectId;
    //TODO use USER instead of just their ID?
    private String invitorEmail;
}
