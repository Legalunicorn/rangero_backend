package com.hiroc.rangero.inviteRecord;


import com.hiroc.rangero.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor
@Slf4j
public class InviteRecordController {

    private final InviteRecordService inviteRecordService;

    //Create record, extract user from SECRUTIY CONTEXT
    //User email + project ID
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InviteRecordDTO createInvite(@Valid @RequestBody InviteRequestDTO request ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication();
        return inviteRecordService.createInvite(request.getEmail(),user,request.getProjectId());
    }

    //user ID + proejct ID
    //The person accepting must =
    @PatchMapping("/${inviteId}/accept")
    public void acceptInvite(@RequestParam long inviteId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication();
        inviteRecordService.acceptInvite(user, inviteId);
    }
}
