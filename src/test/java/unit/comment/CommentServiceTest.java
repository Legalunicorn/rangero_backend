package unit.comment;


import com.hiroc.rangero.comment.*;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    private Task task;
    private Comment comment;
    private User mockUser;
    private CommentRequestDTO request;
    private CommentDTO commentDTO;

    @BeforeEach
    void setUp(){
        String comment_body = "Test comment body";
        long taskId = 1;
        long commentId = 1;
        request = CommentRequestDTO.builder()
                .body(comment_body)
                .taskId(taskId)
                .build();
        task = Task.builder().id(taskId).build();
        comment = Comment.builder()
                .body(comment_body)
                .task(task)
                .build();
        commentDTO = CommentDTO.builder()
                .taskId(taskId)
                .body(comment_body)
                .id(commentId)
                .build();
    }

    @Mock
    private TaskService taskService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createCommentShouldSaveAndReturnComment(){
        //Arrange
        when(taskService.getTaskByIdAuthorized(mockUser,request.getTaskId())).thenReturn(task);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDTO(any(Comment.class))).thenReturn(commentDTO);

        //Act
        CommentDTO createdCommentDTO = commentService.createComment(mockUser,request);

        //Assert
        assertNotNull(createdCommentDTO);
        assertEquals(createdCommentDTO.getBody(),request.getBody());
        assertEquals(createdCommentDTO.getTaskId(),request.getTaskId());

        verify(commentRepository,times(1)).save(any(Comment.class));
        verify(taskService,times(1)).getTaskByIdAuthorized(mockUser,request.getTaskId());
    }


}
