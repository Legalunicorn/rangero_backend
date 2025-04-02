package com.hiroc.rangero.notification;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class NotificationEvent extends ApplicationEvent { //method will be events

    private NotificationRequest request;

    //Validation here (?)
    public NotificationEvent(Object source, @Valid NotificationRequest request) {
        super(source);
        this.request = request;
    }
}
