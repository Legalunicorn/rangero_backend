package com.hiroc.rangero.task.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiroc.rangero.task.enums.TaskPriority;
import com.hiroc.rangero.task.enums.TaskStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class TaskRequestDTO {
    @NotNull
    @Size(min=1)
    private String title;
    @JsonProperty("due_date")
    private LocalDate dueDate;
    //Nullable
    private TaskPriority priority;



    //Nullable
    private TaskStatus status;
    //Nullable
    @JsonProperty("assignee_email")
    @Email
    private String assigneeEmail;
    //Nullable
    @JsonProperty("task_dependencies")
    private Set<Long> taskDependencies;

    @JsonProperty("project_id")
    @Positive
    private Long projectId;
}
