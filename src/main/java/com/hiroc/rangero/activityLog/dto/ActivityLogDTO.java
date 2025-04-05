package com.hiroc.rangero.activityLog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiroc.rangero.activityLog.Action;
import com.hiroc.rangero.task.enums.TaskStatus;
import com.hiroc.rangero.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ActivityLogDTO {
    @JsonProperty("project_id")
    private long projectId;
    @JsonProperty("task_id")
    private long taskId;

    //Should be mapped by the mapper
    private UserDTO user;

    @JsonProperty("assignee_email")
    private String assigneeEmail;
    private Action action;
    @JsonProperty("previous_task_status")
    private TaskStatus previousTaskStatus;
    @JsonProperty("current_task_status")
    private TaskStatus currentTaskStatus;
    @JsonProperty("created_on")
    private LocalDateTime createdOn;


}

