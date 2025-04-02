package com.hiroc.rangero.notification;

import com.hiroc.rangero.notification.dto.NotificationRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class NotificationEvent extends ApplicationEvent { //method will be events

    private NotificationRequest request;
//    private Set<String> validEmails;

    //Validation here (?)
    public NotificationEvent(Object source, @Valid NotificationRequest request) {
        super(source);
        this.request = request;
//        this.validEmails = validEmails;
    }
}
