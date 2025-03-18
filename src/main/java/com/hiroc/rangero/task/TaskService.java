package com.hiroc.rangero.task;


import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.mapper.TaskMapper;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.project.ProjectService;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectMemberService projectMemberService;
    private final ProjectService projectService;
    private final TaskMapper taskMapper;

    @Transactional
    public Task createTask(User user, TaskRequestDTO request){
        log.info(">>> Creating tasks for project with ID: {}",request.getProjectId());
        //Check that the project exists?
        Project project = projectService.findById(request.getProjectId())
                .orElseThrow(()-> new BadRequestException(">>> Project does not exist"));

        Task newTask =  Task.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .priority(request.getPriority())
                .status(request.getStatus())
                .project(project)
                .build();

        //Check that the assignee, if not null ,is a valid person
        if (request.getAssigneeEmail()!=null) {
            String email = request.getAssigneeEmail();
            User assignee = userService.findByEmail(email);
            if (assignee==null){
                throw new BadRequestException(">>> User with email "+email+" does not exist");
            }
            ProjectMember member = projectMemberService.findByUserEmailAndProjectId(email,request.getProjectId())
                    .orElseThrow(()-> new BadRequestException(">>> User with email " +email+ " is not a member of this project"));
            log.info(">>> Assigning new task to : {}",assignee.getUsername());
            newTask.setAssignee(assignee);
        } else{
            log.info(">>> request has no assignee email");
        }
        taskRepository.save(newTask);
        log.debug("Task created");
        return newTask;

    }
}
