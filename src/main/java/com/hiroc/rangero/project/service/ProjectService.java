package com.hiroc.rangero.project.service;


import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.project.ProjectRepository;
import com.hiroc.rangero.project.dto.ProjectRequestDTO;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.projectMember.ProjectRole;
import com.hiroc.rangero.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberService projectMemberService;
//    private final TaskService taskService;
//    private final TaskMapper taskMapper;

    public Optional<Project> findById(long projectId){
        return projectRepository.findById(projectId);
    }

//
//    public ProjectStatsDTO getProjectStats(long projectId, User accessor){
//        //only members can access the user stats
//        ProjectMember member = projectMemberService.findByUserEmailAndProjectId(accessor.getEmail(), projectId)
//                .orElseThrow(UnauthorisedException::new);
//
//        Set<TaskStatusCountDTO> taskCount = taskSer
//        //1. get the count of task
//        // either use a SQL query for each, or loop over all the tasks
//        // I think: 1 sql query, then loop through all with counters
//        return ProjectStatsDTO.builder().build();
//    }

    //Controller handle DTOS now
    @Transactional
    public Project createProject(ProjectRequestDTO request, User creator){
        //Create the project with author
        log.info("Creating project of name: {}",request.getName());
        Project newProject = Project.builder()
                .creator(creator)
                .name(request.getName())
                .strictMode(false)
                .build();

        projectRepository.save(newProject); //Generate ID

        //Create a member -> should delegate to projectMemberSevice?..
        ProjectMember member = ProjectMember.builder()
                .projectRole(ProjectRole.OWNER)
                .project(newProject)
                .user(creator)
                .build();


        newProject.addProjectMember(member);
//        projectRepository.save(newProject);
        //@Transaction no need to save?
        return newProject;
    }




}
