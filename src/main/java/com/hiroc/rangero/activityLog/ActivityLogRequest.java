package com.hiroc.rangero.activityLog;


import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.task.TaskStatus;
import com.hiroc.rangero.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

//@Value
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogRequest {
//    @NotNull - can be null for only task creation, validate inside task service
    private Task task;
    @NotNull
    private Project project;
    @NotNull
    private User user;

    @NotNull
    private Action action;

    private TaskStatus previousTaskStatus;
    private TaskStatus currentTaskStatus;

    private String assignedEmail;

}
