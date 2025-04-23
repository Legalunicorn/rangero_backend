package com.hiroc.rangero.project.service;

import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.project.dto.ProjectStatsDTO;
import com.hiroc.rangero.project.dto.TaskStatusCountDTO;
import com.hiroc.rangero.project.dto.UserTaskCountDTO;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
/*
Abstraction to another layer to prevent circular dependency
 */
public class ProjectStatisticService {
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectMemberService projectMemberService;


    public ProjectStatsDTO getProjectStats(User accessor, long projectId){
        // for safety, verfify the proejct exiss
        Project project = projectService.findById(projectId)
                .orElseThrow(()->new BadRequestException("Project with id of "+projectId+" was not found"));

        ProjectMember member = projectMemberService.findByUserEmailAndProjectId(accessor.getEmail(),projectId)
                .orElseThrow(UnauthorisedException::new);

        // get the status count
        Set<TaskStatusCountDTO> statusCount = taskService.getTaskStatusCount(accessor,projectId);
        Set<UserTaskCountDTO> userTaskCount = taskService.getUserTaskCount(accessor,projectId);

        return ProjectStatsDTO.builder()
                .assignedDistribution(userTaskCount)
                .statusDistribution(statusCount)
                .build();
        // then we also need the
    }
}
