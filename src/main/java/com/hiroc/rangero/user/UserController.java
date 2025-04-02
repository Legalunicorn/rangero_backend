package com.hiroc.rangero.user;


import com.hiroc.rangero.inviteRecord.InviteRecord;
import com.hiroc.rangero.inviteRecord.InviteRecordDTO;
import com.hiroc.rangero.inviteRecord.InviteRecordService;
import com.hiroc.rangero.notification.NotificationService;
import com.hiroc.rangero.notification.dto.NotificationDTO;
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
    private final NotificationService notificationService;

    @GetMapping("/{email}/notifications")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<NotificationDTO> getAllNotifications(@PathVariable String email){
        User accessor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationService.getUserNotifications(accessor,email);
    }

    @GetMapping("/{email}/invites")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<InviteRecordDTO> getAllInvites(@PathVariable String email){
        User accessor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return inviteRecordService.getAllInvites(accessor,email);
    }



}
