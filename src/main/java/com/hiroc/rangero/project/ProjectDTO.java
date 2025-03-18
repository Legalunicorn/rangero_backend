package com.hiroc.rangero.project;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String name;
    //CANT I JUST USE A USER DTO, might cause circular dependency with mappers
    @JsonProperty("creator_email")
    private String creatorEmail;
    @JsonProperty("creator_username")
    private String creatorUsername;

}
