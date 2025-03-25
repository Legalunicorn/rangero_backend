package com.hiroc.rangero.activityLog;


import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.task.TaskStatus;
import com.hiroc.rangero.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ActivityLogRequest {
    @NotNull
    Task task;
    @NotNull
    Project project;
    @NotNull
    User user;

    @NotNull
    Action action;

    TaskStatus previousTaskStatus;
    TaskStatus currentTaskStatus;

}
