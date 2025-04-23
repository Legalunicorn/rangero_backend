package com.hiroc.rangero.project.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
public class ProjectStatsDTO {
    private long projectId;

    //how many of each task
//    private int unassigned;
//    private int todo;
//    private int reviewing;
//    private int completed;

    //you should probably order this some way or another
    private Set<UserTaskCountDTO> assignedDistribution;
    private Set<TaskStatusCountDTO> statusDistribution;

}
