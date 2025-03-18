package com.hiroc.rangero.task;


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
