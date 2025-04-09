package com.hiroc.rangero.user;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Builder
@AllArgsConstructor
@Getter
@Setter
public class UserSettings {

    private boolean AssignmentEmailEnabled;
    private boolean NotificationEmailEnabled;
}
