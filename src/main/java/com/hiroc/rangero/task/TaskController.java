package com.hiroc.rangero.task;


import com.hiroc.rangero.mapper.TaskMapper;
import com.hiroc.rangero.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/tasks")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO createTask(@Valid @RequestBody TaskRequestDTO request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Task taskCreated = taskService.createTask(user,request);
        return taskMapper.toDto(taskCreated);
    }
}
