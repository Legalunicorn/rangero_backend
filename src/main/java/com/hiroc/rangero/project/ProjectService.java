package com.hiroc.rangero.project;


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

    public Optional<Project> findById(long projectId){
        return projectRepository.findById(projectId);
    }

    @Transactional
    public ProjectDTO createProject(ProjectRequestDTO request, User creator){
        //Create the project with author
        Project newProject = Project.builder()
                .creator(creator)
                .name(request.getName())
                .build();

        projectRepository.save(newProject); //Generate ID

        ProjectMember member = ProjectMember.builder()
                .projectRole(ProjectRole.OWNER)
                .project(newProject)
                .user(creator)
                .build();


        newProject.addProjectMember(member);
        //THIS CALL IS NOT NEEDED
        //@Transactional -> any changes automatically FLUSHED to the database at the end of the transaction
        //projectRepository.save(newProject);

        return ProjectDTO.builder()
                .name(request.getName())
                .build();

    }
}
