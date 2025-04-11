package com.hiroc.rangero.user;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor
@Getter
public class RegistrationOTPRateLimit {

    @CollectionTable(
            name="user_otp_attempts",
            joinColumns = @JoinColumn(name="user_id")
    )
    @ElementCollection
    private List<LocalDateTime> previousAttemptsTime;

    public boolean isRateLimited(){
        if (previousAttemptsTime==null) return false;
        //remove attempts older than 5 minutes
        previousAttemptsTime.removeIf(n->n.isBefore(LocalDateTime.now().minusMinutes(5)));
        //TODO change hardcoded rate limit of "5" to be a application property
        return previousAttemptsTime.size()>5;
    }


    //I wonder
    public void addAttempt(){
        if (previousAttemptsTime==null) previousAttemptsTime=new ArrayList<>();
        previousAttemptsTime.add(LocalDateTime.now());
    }


}
