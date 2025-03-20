package com.hiroc.rangero.project;


import com.hiroc.rangero.mapper.ProjectMapper;
import com.hiroc.rangero.task.TaskDTO;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectMapper projectMapper;

    @GetMapping("/{projectId}/tasks")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<TaskDTO> getProjectTasks(@PathVariable long projectId){
        User accessor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.findTaskByProjectId(accessor,projectId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDTO createProject(@Valid @RequestBody ProjectRequestDTO request){
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Project newProject =  projectService.createProject(request,owner);
        log.info("created project name: {}",newProject.getName());
        return projectMapper.toDTO(newProject);
    }
}
