package com.hiroc.rangero.inviteRecord;


import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.project.ProjectService;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.projectMember.ProjectRole;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteRecordService {

    private final InviteRecordRepository inviteRecordRepository;
    private final ProjectMemberService projectMemberService;
    private final UserService userService;
    private final ProjectService projectService;

    public InviteRecord createInvite(String inviteeEmail, User invitor, long projectId){

        //0. check that both project and invitee Email exists
        User inviteeUser = userService.findByEmail(inviteeEmail);
        if (inviteeEmail==null) throw new BadRequestException("Email of user not found");
        //This is kind of redundant, because the client will only feed a projectId that WE sent them
        Project project = projectService.findById(projectId)
                .orElseThrow(()-> new BadRequestException("Project of ID does not exist"));

        //1. verify the invitor has authority
        ProjectMember invitorRecord = projectMemberService.findByUserAndProject(invitor,project)
                .orElseThrow(()-> new UnauthorisedException("Invitation failed. Invitor is not a member of the project"));
        checkProjectPermissions(project,invitorRecord,ProjectRole.ADMIN);

        //2. check that invitee is not already a member
        Optional<ProjectMember> existingMember = projectMemberService.findByUserAndProject(inviteeUser,project);
        if (existingMember.isPresent()) throw new BadRequestException("User is already a member");

        //3. Create the invite record
        InviteRecord newInvite = InviteRecord.builder()
                .inviteStatus(InviteStatus.PENDING)
                .project(project)
                .invitee(inviteeUser)
                .invitor(invitor)
                .build();

        inviteRecordRepository.save(newInvite);

        return newInvite;

    }


    @Transactional
    public void acceptInvite(User user, long inviteId) {
        InviteRecord record = inviteRecordRepository.findById(inviteId)
                .orElseThrow(()-> new BadRequestException("Invite request does not exist"));

        if (record.getInvitee().getId()!=user.getId()){
            throw new UnauthorisedException("You do not have permission to accept this invite");
        }

        if (record.getInviteStatus()!=InviteStatus.PENDING){
            throw new BadRequestException("Invite requet has expired or is not pending.");
        }

        Project project = record.getProject();

        if (projectMemberService.findByUserAndProject(user,project).isPresent()){
            throw new BadRequestException("You are already a member of project: "+project.getName());
        }

        ProjectMember newMember = ProjectMember
                .builder()
                .projectRole(ProjectRole.MEMBER)
                .user(user)
                .project(project)
                .build();
        projectMemberService.save(newMember);
        record.setInviteStatus(InviteStatus.ACCEPTED);

    }

    private void checkProjectPermissions(Project project,ProjectMember projectMember,ProjectRole requiredRole){
        if (project.isStrictMode()){
            if (!projectMember.getProjectRole().hasPermission(requiredRole)) {
                throw new UnauthorisedException("You do not have permission to perform this action");
            }
        }
    }

//    private void checkProjectPermissions(Project project, User user, ProjectRole requiredRole){
//        if (project.isStrictMode()){
//            //Get the role of the user in this project?
//            ProjectMember projectMember = projectMemberService.findByUserAndProject(user,project)
//                    .orElseThrow(()->new UnauthorisedException("user is not a member of the project."));
//            if (!projectMember.getProjectRole().hasPermission(requiredRole)){
//                throw new UnauthorisedException("You do not have permission to perform this action");
//            }
//        }
//    }
}
