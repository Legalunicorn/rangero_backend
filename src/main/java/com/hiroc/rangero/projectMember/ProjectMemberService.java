package com.hiroc.rangero.projectMember;


import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberMapper projectMemberMapper;

    public Set<ProjectMember> findByUserEmailsInAndProject(Project project, Set<String> emails) {
        return projectMemberRepository.findByUserEmailsAndProject(project, emails);
    }


    @Transactional
    public void removeMember(User authorizingUser,Long projectId,Long userId){
        ProjectMember authorizingMember = projectMemberRepository.findByUserEmailAndProjectId(authorizingUser.getEmail(),projectId)
                .orElseThrow(UnauthorisedException::new);
        ProjectMember affectedMember = projectMemberRepository.findByUserIdAndProjectId(userId,projectId)
                .orElseThrow(()->new BadRequestException("User is not a member of the project"));

        //CHECK FOR SELF REMOVE
        if (authorizingUser.getId()==userId){
            if (authorizingMember.getProjectRole()==ProjectRole.OWNER){
                throw new BadRequestException("Please transfer ownership before leaving, or delete the project ");
            } else{
                projectMemberRepository.delete(affectedMember);
            }
        }

        //MEMBER cannot remove anyone but themselves
        if (authorizingMember.getProjectRole()==ProjectRole.MEMBER ) throw new UnauthorisedException();

        //ADMIN can only remove members, not self remove
        if (authorizingMember.getProjectRole()==ProjectRole.ADMIN){
            if (affectedMember.getProjectRole()==ProjectRole.MEMBER){
                projectMemberRepository.delete(affectedMember);
            } else{
                throw new UnauthorisedException();
            }
        }

        //OWNERS , and not self remove
        if (authorizingMember.getProjectRole()==ProjectRole.OWNER){
            projectMemberRepository.delete(affectedMember);
        }
    }



    @Transactional
    public ProjectMember save(ProjectMember projectMember){
        return projectMemberRepository.save(projectMember);
    }



    public Optional<ProjectMember> findByUserAndProject(User user, Project project){
        return projectMemberRepository.findByUserAndProject(user,project);
    }



    public Optional<ProjectMember> findByUserEmailAndProjectId(String email, long projectId) {
        return projectMemberRepository.findByUserEmailAndProjectId(email,projectId);
    }



    public Optional<ProjectMember> getProjectMember(String user_email,Long projectId){
        return projectMemberRepository.findByUserEmailAndProjectId(user_email,projectId);
    }



    public Set<ProjectMemberDTO> getAllProjectMember(User accessor, Long projectId){
        ProjectMember member = getProjectMember(accessor.getEmail(),projectId)
                .orElseThrow(UnauthorisedException::new);

        Set<ProjectMember> allMembers =  projectMemberRepository.findAllProjectMembers(projectId);
        return allMembers.stream().map(projectMemberMapper::toDTO).collect(Collectors.toSet());
    }



    @Transactional
    public void patchProjectMemberRole(User authorizingUser, Long projectId, Long userId, ProjectRole newRole) {
        //get the authorizingUser role
        ProjectMember authorizingMember = projectMemberRepository.findByUserEmailAndProjectId(authorizingUser.getEmail(),projectId)
                .orElseThrow(UnauthorisedException::new);
        //Member are not allowed to change roles
        if (authorizingMember.getProjectRole()==ProjectRole.MEMBER){
            throw new UnauthorisedException();
        }
        ProjectMember affectedMember = projectMemberRepository.findByUserIdAndProjectId(userId,projectId)
                .orElseThrow(()-> new BadRequestException("Affected user is not part of the project"));

        if (authorizingMember.getProjectRole()==ProjectRole.OWNER){
            //If they choose someone else to be the OWNER, they will be knocked down to admin
            if (newRole==ProjectRole.OWNER){
                authorizingMember.setProjectRole(ProjectRole.ADMIN);
                affectedMember.setProjectRole(ProjectRole.OWNER);
            } else{ //the new role is something below OWNER, and affected is something isnt OWNER => any aation is allowed
                affectedMember.setProjectRole(newRole);
            }
        } else if (authorizingMember.getProjectRole()==ProjectRole.ADMIN){ //can only promote a member to admin
            if (newRole==ProjectRole.ADMIN && affectedMember.getProjectRole()==ProjectRole.MEMBER){
                affectedMember.setProjectRole(ProjectRole.ADMIN);
            } else throw new UnauthorisedException();
        }


    }


//    private
}
