package com.hiroc.rangero.activityLog;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ActivityLogEvent extends ApplicationEvent {

    private ActivityLogRequest activity;

    public ActivityLogEvent(Object source,ActivityLogRequest activity) {
        super(source);
        this.activity=activity;
    }
}
