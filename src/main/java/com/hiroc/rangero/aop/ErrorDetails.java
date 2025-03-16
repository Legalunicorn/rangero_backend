package com.hiroc.rangero.aop;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
public class ErrorDetails {
    //the controller advice will handle the HTTP CODE
    //this is mainly for the message + timestamp encapsulation
    private String message;
    private String code; //ERROR CODE to group errors or provide documentation help
    private Instant timestamp = Instant.now();

    public ErrorDetails(String message){
        this.message = message;
        this.timestamp = Instant.now();
    }

    public ErrorDetails(String message, String code){
        this.message = message;
        this.timestamp = Instant.now();
        this.code = code;
    }
}
