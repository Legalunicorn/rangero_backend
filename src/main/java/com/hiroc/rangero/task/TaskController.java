package com.hiroc.rangero.task;


import com.hiroc.rangero.comment.CommentDTO;
import com.hiroc.rangero.comment.CommentService;
import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.task.dto.TaskDTO;
import com.hiroc.rangero.task.dto.TaskRequestDTO;
import com.hiroc.rangero.task.helper.TaskMapper;
import com.hiroc.rangero.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/tasks")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;
    private final TaskMapper taskMapper;


    @GetMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Set<CommentDTO> getTaskComments(@PathVariable long taskId){
        if (taskId<=0){
            throw new BadRequestException("Task Id must be positive");
        }
        User accessor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return commentService.getCommentsByTaskIdAuthorizedToDto(accessor,taskId);
    }



    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaskDTO getTask(@PathVariable long taskId){
        User accessor = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.getTaskByIdAuthorizedToDto(accessor,taskId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO createTask(@Valid @RequestBody TaskRequestDTO request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Task taskCreated = taskService.createTask(user,request);
        return taskMapper.toDto(taskCreated);
    }

//    //TOD remove if useless
//    @PatchMapping("/{taskId}/status")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public TaskDTO patchTaskStatus(@PathVariable Long taskId, @RequestBody TaskRequestDTO request ){
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (request.getStatus()==null){
//            throw new BadRequestException("Invalid updated status value");
//        }
//        if (taskId==null || taskId<=0) throw new BadRequestException("Invalid taskId");
//        return taskService.patchTaskStatusDto(user,taskId,request.getStatus());
//    }

    @PatchMapping("/{taskId}/details")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaskDTO patchTaskDetails(@PathVariable long taskId,@RequestBody TaskRequestDTO request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (taskId<=0) throw new BadRequestException("Invalid taskId");
        return taskService.patchTaskDetails(user,taskId,request);
    }
}
