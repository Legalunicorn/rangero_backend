package com.hiroc.rangero.project;


import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.mapper.TaskMapper;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberDTO;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.projectMember.ProjectRole;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskDTO;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
