package com.hiroc.rangero.comment;


import com.hiroc.rangero.project.ProjectService;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.utility.FileService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final CommentMapper commentMapper;
    private final FileService fileService;

    public Set<CommentDTO> getCommentsByTaskIdAuthorizedToDto(User accessor, Long taskId){
        //1. gets the tasks
        // => verifies the tasks exists
        // => verifies accessor belongs to project of the task
        Task task = taskService.getTaskByIdAuthorized(accessor, taskId);

        Set<Comment> taskComments = commentRepository.findByTask(task);
        return taskComments.stream().map(commentMapper::toDTO).collect(Collectors.toSet());

    }

    //Permissions: any member
    public CommentDTO createComment(User creator, CommentRequestDTO request) {
        //Get the task, ensuring accessor is part of the project
        Task task = taskService.getTaskByIdAuthorized(creator,request.getTaskId());
        //Create comment
        Comment newComment = Comment.builder()
                .task(task)
                .body(request.getBody())
                .build();
        commentRepository.save(newComment);
        return commentMapper.toDTO(newComment);

    }

    public CommentDTO createComment2(User creator, @Valid CommentRequestDTO request, MultipartFile file) throws IOException {
        //Get task and check permissions
        Task task = taskService.getTaskByIdAuthorized(creator,request.getTaskId());
        Comment newComment = Comment.builder()
                .task(task)
                .body(request.getBody())
                .build();
        //Check for file upload
        if (file!=null){
            String key = fileService.uploadFile(file,request.getTaskId());
            //Success ?!?1
            log.debug("file key: {}",key);
            log.debug("file name: {}",file.getOriginalFilename());
            log.debug("file size: {}",file.getSize());
            newComment.setFileKey(key);
            newComment.setFileName(file.getOriginalFilename());
            newComment.setFileSize(file.getSize());
        }
        commentRepository.save(newComment);
        return commentMapper.toDTO(newComment);
    }
}
