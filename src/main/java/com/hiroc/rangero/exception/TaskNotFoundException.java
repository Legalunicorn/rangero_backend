package com.hiroc.rangero.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(Long taskId){
        super("Task with id: "+taskId+" was not found.");
    }
}
