package com.hiroc.rangero.task.dto;


import com.hiroc.rangero.task.enums.TaskPriority;
import com.hiroc.rangero.task.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;


@Data
public class TaskDTO {
    private Long id;
    private String title;
    private LocalDate dueDate;
    private TaskPriority priority;
    private TaskStatus status;
    private Long assigneeId;
}
