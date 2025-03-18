package com.hiroc.rangero.task;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class TaskRequestDTO {
    @NotNull
    private String title;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    //Nullable
    private TaskStatus status;

    //Nullable
    private TaskPriority priority;

    //Nullable
    @JsonProperty("assignee_email")
    private String assigneeEmail;

    @JsonProperty("project_id")
    private Long projectId;
}
