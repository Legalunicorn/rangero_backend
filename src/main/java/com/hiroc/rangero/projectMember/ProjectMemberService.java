package com.hiroc.rangero.projectMember;


import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ProjectMember save(ProjectMember projectMember){
        return projectMemberRepository.save(projectMember);
    }

    public Optional<ProjectMember> findByUserAndProject(User user, Project project){
        return projectMemberRepository.findByUserAndProject(user,project);
    }
}
