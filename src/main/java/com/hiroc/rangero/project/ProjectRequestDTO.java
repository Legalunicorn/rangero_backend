package com.hiroc.rangero.project;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
//TODO check if ProjectDTO changes, else this is the same thing
public class ProjectRequestDTO {
    @Size(min=1,max=100)
    private String name;
}
