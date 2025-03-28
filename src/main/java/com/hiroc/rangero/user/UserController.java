package com.hiroc.rangero.user;


import com.hiroc.rangero.inviteRecord.InviteRecord;
import com.hiroc.rangero.inviteRecord.InviteRecordDTO;
import com.hiroc.rangero.inviteRecord.InviteRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final InviteRecordService inviteRecordService;

    @GetMapping("/{email}/invites")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<InviteRecordDTO> getAllInvites(@PathVariable String email){
        User accessor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return inviteRecordService.getAllInvites(accessor,email);
    }



}
