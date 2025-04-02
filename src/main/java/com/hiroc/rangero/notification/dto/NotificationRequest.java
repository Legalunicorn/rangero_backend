package com.hiroc.rangero.notification.dto;


import com.hiroc.rangero.notification.NotificationType;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class NotificationRequest {

    //NotificationRequest contains the entity already?
    // we need to mofigy the notification event
    @NotNull
    private User sender;

    @NotNull
    private Set<User> validUsers;

    @NotNull
    private Task task;

    private NotificationType notificationType;




//    @NotNull
//    private User sender;
//    @NotNull
//    private User receiver;
//    @NotNull
//    private Task task;
//    @NotNull
//    private NotificationType notificationType;
//    //open default to false?

}
