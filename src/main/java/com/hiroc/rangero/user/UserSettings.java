package com.hiroc.rangero.user;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSettings {

    private boolean AssignmentEmailEnabled= true;
    private boolean NotificationEmailEnabled= true;
}
