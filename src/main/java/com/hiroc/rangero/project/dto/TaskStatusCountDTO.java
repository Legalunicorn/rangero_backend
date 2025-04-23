package com.hiroc.rangero.project.dto;

import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.task.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@AllArgsConstructor
public class TaskStatusCountDTO {
    private TaskStatus status;
    private long count;

    public TaskStatusCountDTO(TaskStatus status, long count){
        this.status = status;
        this.count = count;
    }
}
