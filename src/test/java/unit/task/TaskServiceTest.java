package unit.task;


import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.task.helper.TaskMapper;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.project.ProjectService;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.projectMember.ProjectRole;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskRepository;
import com.hiroc.rangero.task.dto.TaskRequestDTO;
import com.hiroc.rangero.task.TaskService;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    //Mock dependencies
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;
    @Mock
    private ProjectMemberService projectMemberService;
    @Mock
    private ApplicationEventPublisher eventPublisher ;
    @Mock
    private ProjectService projectService;
    @Mock
    private TaskMapper taskMapper;

    //Mock object
    @InjectMocks
    private TaskService taskService;


    //mock variables
    private User mockUser;
    private Task mockTask;
    private ProjectMember mockMember;
    private Project mockProject;

    @BeforeEach
    void setUp(){
        mockProject = Project.builder()
                .id(1L)
                .build();
        mockUser = User.builder()
                .email("test@gmail.com")
                .username("test")
                .build();
        mockTask = Task.builder()
                .id(1)
                .project(mockProject)
                .build();
        mockMember = ProjectMember.builder()
                .user(mockUser)
                .project(mockProject)
                .projectRole(ProjectRole.MEMBER)
                .build();
    }

    @Test
    void getTaskByIdAuthorizedHappyFlow(){
        //Arrange
        // I dont understand this unit test, becase the arrange will ALWAYS result in the assertion being true
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(mockTask)); // I dont understand this un
        when(projectMemberService.getProjectMember(any(String.class),any(Long.class))).thenReturn(Optional.ofNullable(mockMember));
        //Act
        Task returnedTask = taskService.getTaskByIdAuthorized(mockUser,mockTask.getId());

        //Assert
        assertEquals(returnedTask.getId(),mockTask.getId());
        verify(taskRepository,times(1)).findById(any(Long.class));
        verify(projectMemberService,times(1)).getProjectMember(any(String.class),any(Long.class));
    }

    @Test
    void getTaskByIdAuthorizedFailedAuthorization(){
        //Arrange
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(mockTask)); // I dont understand this un
        when(projectMemberService.getProjectMember(any(String.class),any(Long.class))).thenThrow(UnauthorisedException.class);

        //Assert
        assertThrows(UnauthorisedException.class,()->taskService.getTaskByIdAuthorized(mockUser,mockTask.getId()));
    }

    @Test
    void createTaskHappyFlowStrictMode(){
        //Arrange
        mockProject.setStrictMode(true); //Strict MODE
        mockMember.setProjectRole(ProjectRole.ADMIN);
        TaskRequestDTO mockRequest = TaskRequestDTO
                .builder()
                .title("mock title")

                .projectId(mockProject.getId())
                //Assignee email null to skip that check
                .build();

        when(projectService.findById(any(Long.class))).thenReturn(Optional.ofNullable(mockProject));
        when(projectMemberService.findByUserAndProject(any(User.class),any(Project.class))).thenReturn(Optional.ofNullable(mockMember));
//        when(taskRepository.save(any(Task.class))).

        //Act
        Task createdTask = taskService.createTask(mockUser,mockRequest);

        //Assert
        assertEquals(createdTask.getTitle(),mockRequest.getTitle());

    }

    @Test
    void createTaskRoleFailedStrictMode(){
        //Arrange
        mockProject.setStrictMode(true); //Strict MODE
        mockMember.setProjectRole(ProjectRole.MEMBER);
        TaskRequestDTO mockRequest = TaskRequestDTO
                .builder()
                .title("mock title")
                .projectId(mockProject.getId())
                //Assignee email null to skip that check
                .build();

        when(projectService.findById(any(Long.class))).thenReturn(Optional.ofNullable(mockProject));
        when(projectMemberService.findByUserAndProject(any(User.class),any(Project.class))).thenReturn(Optional.ofNullable(mockMember));

        //Act, Assert?
        assertThrows(UnauthorisedException.class,()->taskService.createTask(mockUser,mockRequest));


    }
}





























