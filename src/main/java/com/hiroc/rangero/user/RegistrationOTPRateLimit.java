package com.hiroc.rangero.user;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
import java.util.List;

@Embeddable
public class RegistrationOTPRateLimit {

    @CollectionTable(
            name="user_otp_attempts",
            joinColumns = @JoinColumn(name="user_id")
    )
    @ElementCollection
    //I wonder if using "local date time" will be problematic in a microservice architecture with load balancing
    // because each server maybe be running in a different time zone
    private List<LocalDateTime> previousAttemptsTime;

    public boolean isRateLimited(){
        previousAttemptsTime.removeIf(n->n.isBefore(LocalDateTime.now().minusMinutes(5)));
        //TODO change hardcoded rate limit of "5" to be a application property
        return previousAttemptsTime.size()>5;
    }


    //I wonder
    public void addAttempt(){
        previousAttemptsTime.add(LocalDateTime.now());
    }
}
