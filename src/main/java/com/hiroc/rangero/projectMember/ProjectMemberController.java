package com.hiroc.rangero.projectMember;

import com.hiroc.rangero.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PatchMapping("/project/{projectId}/user/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void patchProjectMemberRole(@PathVariable Long projectId, @PathVariable Long userId, @RequestBody ProjectRole newRole){
        User authorizingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectMemberService.patchProjectMemberRole(authorizingUser, projectId, userId, newRole);
    }

    @DeleteMapping("/project/{projectId}/user/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeProjectMember(@PathVariable Long projectId, @PathVariable Long userId){
        User authorizingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectMemberService.removeMember(authorizingUser,projectId,userId);
    }

}
