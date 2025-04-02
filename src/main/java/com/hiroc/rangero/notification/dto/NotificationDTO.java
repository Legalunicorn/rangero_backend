package com.hiroc.rangero.notification.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiroc.rangero.notification.NotificationType;
import com.hiroc.rangero.user.dto.UserDTO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter

public class NotificationDTO {
    private long id;
    private UserDTO sender;
    private UserDTO receiver;
    @JsonProperty("task_id")
    private long taskId;

    @JsonProperty("notification_type")
    private NotificationType notificationType;

    @JsonProperty("created_on")
    private LocalDateTime createdOn;

}
