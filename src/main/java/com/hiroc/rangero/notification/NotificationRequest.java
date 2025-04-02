package com.hiroc.rangero.notification;


import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NotificationRequest {
    //use user_id to create notification ? or use email/
    //the frontend will not have exposure to the other user id will they
//    @Email
//    private String senderEmail;
//    @Email
//    private String receiverEmail;
//    @NotNull
//    private Long taskId;

    //NotificationRequest contains the entity already?
    @NotNull
    private User sender;
    @NotNull
    private User receiver;
    @NotNull
    private Task task;
    @NotNull
    private NotificationType notificationType;
    //open default to false?

}
