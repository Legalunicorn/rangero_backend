package com.hiroc.rangero.task;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Size(min=1)
    private String title;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    //Nullable
    private TaskStatus status;

    //Nullable
    private TaskPriority priority;

    //Nullable
    @JsonProperty("assignee_email")
    @Email
    private String assigneeEmail;

    @JsonProperty("project_id")
    @Positive
    private Long projectId;
}
