package com.hiroc.rangero.task;


import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.mapper.TaskMapper;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.project.ProjectService;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.projectMember.ProjectRole;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectMemberService projectMemberService;

    //Make sure project service does not call task service
    private final ProjectService projectService;
    private final TaskMapper taskMapper;

    public Set<TaskDTO> findTaskByProjectId(User accessor, Long projectId){
        ProjectMember member = projectMemberService.getProjectMember(accessor.getEmail(),projectId)
                .orElseThrow(UnauthorisedException::new);

         Set<Task> tasks = taskRepository.findTasksByProjectId(projectId);
         return tasks.stream().map(taskMapper::toDto).collect(Collectors.toSet());
    }

    @Transactional
    public TaskDTO patchTaskDetails(User user, Long taskId, TaskRequestDTO updatedDetails){
        //Similar to status but wider range of information can be changed is not just the status
        //TODO - add this to the log table when this is done, perhaps using rabbitMQ , or @Async
        //1 .verify tas kID
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new BadRequestException("Task with id " + taskId + " does not exist."));
        //2. check project member role
        ProjectMember member = projectMemberService.findByUserAndProject(user,task.getProject())
                .orElseThrow(()->new UnauthorisedException("You do not have permission to do this action. "));
        //3. check strict mode: ADMIN
        checkProjectPermissions(task.getProject(),member,ProjectRole.ADMIN);
        //4. edit task and save
        if (updatedDetails.getTitle()!=null) task.setTitle(updatedDetails.getTitle());
        if (updatedDetails.getStatus()!=null) task.setStatus(updatedDetails.getStatus());
        if (updatedDetails.getDueDate()!=null) task.setDueDate(updatedDetails.getDueDate());
        if (updatedDetails.getAssigneeEmail()!=null){
            modifyTaskAssignee(updatedDetails.getAssigneeEmail(),task.getProject(),task);
        }
        if (updatedDetails.getPriority()!=null) task.setPriority(updatedDetails.getPriority());
        log.info("Task with ID {} updated by user {}", taskId, user.getUsername());
        return taskMapper.toDto(task);

    }

    public TaskDTO patchTaskStatusDto(User user, Long taskId, TaskStatus newStatus){
        Task updatedTask = patchTaskStatus(user,taskId,newStatus);
        return taskMapper.toDto(updatedTask);
    }
    @Transactional
    public Task patchTaskStatus(User user, Long taskId, TaskStatus newStatus){
        //0. User has been authenticated by JWT + status required no permissions
        //1. verify the taskID exists to retrieve task entity
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new BadRequestException("Task with id "+taskId+" does not exist"));
        //2. use task entity to get the project, and then fetch the projectMember
        ProjectMember member = projectMemberService.findByUserAndProject(user,task.getProject())
                .orElseThrow(()-> new UnauthorisedException("You do not have permission to do this action"));
        //3. modify the task and commit
        task.setStatus(newStatus);
        taskRepository.save(task);
        return task;
    }

    @Transactional
    public Task createTask(User user, TaskRequestDTO request){

        log.info(">>> Creating tasks for project with ID: {}",request.getProjectId());
        //Check that the project exists?
        //TODO perform multiple
        Project project = projectService.findById(request.getProjectId())
                .orElseThrow(()-> new BadRequestException(">>> Project does not exist"));

        ProjectMember taskCreator = projectMemberService.findByUserAndProject(user,project)
                .orElseThrow(()-> new UnauthorisedException(">>> Unauthorized to create task: User is not a member"));

        //strict mode: only admins can proceed to create AND assign
        checkProjectPermissions(project,taskCreator,ProjectRole.ADMIN);

        Task newTask =  Task.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .priority(request.getPriority())
                .status(request.getStatus())
                .project(project)
                .build();

        //Check that the assignee, if not null ,is a valid person
        //TODO - extract this logic
        if (request.getAssigneeEmail()!=null) {
            modifyTaskAssignee(request.getAssigneeEmail(),project,newTask);
        } else{
            log.info(">>> request has no assignee email");
        }
        taskRepository.save(newTask);
        log.debug("Task created");
        return newTask;

    }

    private void modifyTaskAssignee(String newAssigneeEmail, Project project, Task task){
        User newAssignee = userService.findByEmail(newAssigneeEmail);
        if (newAssignee==null) throw new BadRequestException(">>> User with email "+newAssigneeEmail+" does not exist");
        ProjectMember newAssigneeRole = projectMemberService.findByUserAndProject(newAssignee,project)
                .orElseThrow(()->new BadRequestException(">>> User with email "+newAssigneeEmail+" is not a member of this project"));
        log.info(">>> assigning task to: {}", newAssigneeEmail);
        task.setAssignee(newAssignee);

    }

    //TODO - resolve: both project and project member must be fetched every time
    //consider creating this as a bean for flexibility or just manually checking on a case to case basis
    private void  checkProjectPermissions(Project project, ProjectMember projectMember, ProjectRole requiredRole){
        if (project.isStrictMode() && !projectMember.getProjectRole().hasPermission(requiredRole)){
            throw new UnauthorisedException("You do not have permission to perform this action.");
        }
    }


}
