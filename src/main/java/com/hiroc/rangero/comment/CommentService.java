package com.hiroc.rangero.comment;


import com.hiroc.rangero.email.EmailEvent;
import com.hiroc.rangero.email.dto.EmailRequest;
import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.notification.NotificationEvent;
import com.hiroc.rangero.notification.dto.NotificationRequest;
import com.hiroc.rangero.notification.NotificationType;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.projectMember.ProjectRole;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.user.UserService;
import com.hiroc.rangero.utility.FileService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
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
    private final UserService userService;
    private final ProjectMemberService projectMemberService;
    private final ApplicationEventPublisher eventPublisher;

    public Set<CommentDTO> getCommentsByTaskIdAuthorizedToDto(User accessor, Long taskId){
        //1. gets the tasks
        // => verifies the tasks exists
        // => verifies accessor belongs to project of the task
        Task task = taskService.getTaskByIdAuthorized(accessor, taskId);

        Set<Comment> taskComments = commentRepository.findByTask(task);
        return taskComments.stream().map(commentMapper::toDTO).collect(Collectors.toSet());

    }

    @Transactional
    //TODO - allow multiple file uploads (up to 3)
    public CommentDTO createComment(User creator, @Valid CommentRequestDTO request, MultipartFile file) throws IOException {
        //Get task and check permissions
        Task task = taskService.getTaskByIdAuthorized(creator,request.getTaskId());


        Comment newComment = Comment.builder()
                .task(task)
                .body(request.getBody())
                .author(creator)
                .build();

        //Check for mentions
        Set<User> notifiedUsers = new HashSet<>() ;
        if (request.getNotifiedUserEmails()!=null){
            Set<ProjectMember> existingMentionUsersInProject = projectMemberService.findByUserEmailsInAndProject(task.getProject(),request.getNotifiedUserEmails());
            //TO be sent for the event later
            notifiedUsers = existingMentionUsersInProject.stream().map(ProjectMember::getUser).collect(Collectors.toSet());

            Set<String> validEmails = notifiedUsers.stream().map(User::getEmail).collect(Collectors.toSet());
            newComment.setValidEmails(validEmails);

        }

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

        //Publish event to create a notification
        if (!notifiedUsers.isEmpty()){
            emailUsers(creator,notifiedUsers,task);
            notifyUsersInApp(creator,notifiedUsers,task);
        }

        return commentMapper.toDTO(newComment);
    }

    //TODO - delete comment
    // Permission level : Author or >=ADMIN
    @Transactional
    public void deleteComment(long commentId, User userPerformingAction){
        //0. get the comment
        // if user is author: authorized delete and exist
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new BadRequestException("comment not found"));

        if (comment.getAuthor().getId()==userPerformingAction.getId()){
            commentRepository.delete(comment);
            return;
        }

        //Admin and above
        ProjectMember member = projectMemberService.findByUserEmailAndProjectId(userPerformingAction.getEmail(),comment.getTask().getProject().getId())
                .orElseThrow(UnauthorisedException::new);

        if (member.getProjectRole()== ProjectRole.MEMBER){
            throw new UnauthorisedException();
        } else{
            commentRepository.delete(comment);

        }
    }


    //TODO - edit comment


    //TODO - get comment?


    /*
    ############################## HELPERS ##################################
     */


    private void emailUsers(User sender, Set<User> notifiedUsers, Task task){
        for (User recipient: notifiedUsers){
            EmailRequest emailRequest = EmailRequest.builder()
                    .subject(sender.getEmail()+ "Mentioned You In A Comment")
                    .recipient(recipient.getEmail())
                    .body("""
                            Dear %s,
                            
                            %s has mentioned you in a comment under the task: "%s"
                            """.formatted(recipient.getUsername(),sender.getUsername(),task.getTitle()))
                    .build();

            eventPublisher.publishEvent(new EmailEvent(this, emailRequest));
        }
    }

    private void notifyUsersInApp(User sender, Set<User> notifiedUsers, Task task){
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .notificationType(NotificationType.MENTION)
                .sender(sender)
                .task(task)
                .validUsers(notifiedUsers)
                .build();
        eventPublisher.publishEvent(new NotificationEvent(this,notificationRequest));
    }
}
