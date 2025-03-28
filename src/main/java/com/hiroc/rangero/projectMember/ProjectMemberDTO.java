package com.hiroc.rangero.projectMember;


import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProjectMemberDTO {

    private long id;
    private long projectId;
    private long userId;
    private String email;
    private String username;
    private ProjectRole projectRole;

}
