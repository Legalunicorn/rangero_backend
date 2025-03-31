package com.hiroc.rangero.task;


import com.hiroc.rangero.activityLog.Action;
import com.hiroc.rangero.activityLog.ActivityLogEvent;
import com.hiroc.rangero.activityLog.ActivityLogRequest;
import com.hiroc.rangero.activityLog.ActivityLogService;
import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.TaskNotFoundException;
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
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    //Make sure project service does not call task service
    private final ProjectService projectService;
    private final TaskMapper taskMapper;

    public TaskDTO getTaskByIdAuthorizedToDto(User accessor, long taskId){
        Task task = getTaskByIdAuthorized(accessor,taskId);
        return taskMapper.toDto(task);
    }

    public Task getTaskByIdAuthorized(User accessor , long taskID){
        //Security measure: only members can access the task
        //Get the task
        Task task = taskRepository.findById(taskID)
                .orElseThrow(()->new TaskNotFoundException(taskID));
        //Check that the user is a member of the project that the tasks belongs t o
        ProjectMember member = projectMemberService.getProjectMember(accessor.getEmail(),task.getProject().getId())
                .orElseThrow(UnauthorisedException::new);
        //Return the task
        return task;

    }

    public Set<TaskDTO> findTaskByProjectId(User accessor, Long projectId){
        ProjectMember member = projectMemberService.getProjectMember(accessor.getEmail(),projectId)
                .orElseThrow(UnauthorisedException::new);

         Set<Task> tasks = taskRepository.findTasksByProjectId(projectId);
         return tasks.stream().map(taskMapper::toDto).collect(Collectors.toSet());
    }

    @Transactional
    //TODO -> allow task dependencies modification?
    public TaskDTO patchTaskDetails(User user, Long taskId, TaskRequestDTO updatedDetails){
        //Similar to status but wider range of information can be changed is not just the status
        //1 .verify task ID
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new BadRequestException("Task with id " + taskId + " does not exist."));
        //2. check project member role
        ProjectMember member = projectMemberService.findByUserAndProject(user,task.getProject())
                .orElseThrow(()->new UnauthorisedException("You do not have permission to do this action. "));

        //3. check strict mode: ADMIN
        checkProjectPermissions(task.getProject(),member,ProjectRole.ADMIN);

        // Authorized to Proceed
        //4. edit task and save
        if (updatedDetails.getTitle()!=null) {
            task.setTitle(updatedDetails.getTitle());
        }
        if (updatedDetails.getStatus()!=null && task.getStatus()!=updatedDetails.getStatus()) {
            createActivityLog(task,user,task.getProject(),Action.UPDATE_TASK_DETAILS);
            task.setStatus(updatedDetails.getStatus());
        }
        if (updatedDetails.getDueDate()!=null) task.setDueDate(updatedDetails.getDueDate());
        if (updatedDetails.getAssigneeEmail()!=null){
            createActivityLog(task,user,task.getProject(),Action.ASSIGN_TASK);
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
        createActivityLog(task,user,task.getProject(),Action.UPDATE_TASK_STATUS,task.getStatus(),newStatus);
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
        if (request.getAssigneeEmail()!=null) {
            modifyTaskAssignee(request.getAssigneeEmail(),project,newTask);
        } else{
            log.info(">>> request has no assignee email");
        }
        taskRepository.save(newTask);

        //TODO, should this be sync or async, can newTask be used or not
        createActivityLog(newTask,user,project,Action.CREATE_TASK);
        log.debug("Task created");
        return newTask;

    }

    //TODO
    @Transactional
    public void setTaskDependencies(User user, Long projectId, Long taskId, Set<Long> taskDependencyIds){
        // 0 - validate everything: User permission, all tasks existing
        //Set the task depednes base on the set of tasks ids

        //Run the cycle detection algorithm

        //

    }


    // HELPER METHODS #################################################################


    //No Task
    //TODO remove after changing to activity log
    private void createActivityLog(User user, Project project, Action action){
        ActivityLogRequest request = ActivityLogRequest.builder()
                .user(user).project(project).action(action).build();
        eventPublisher.publishEvent(new ActivityLogEvent(this,request));
    }

    //No status change
    private void createActivityLog(Task task, User user,Project project, Action action){
        ActivityLogRequest request = ActivityLogRequest.builder()
                .user(user).project(project).task(task).action(action).build();
        eventPublisher.publishEvent(new ActivityLogEvent(this,request));
    }
    private void createActivityLog(Task task,User user, Project project, Action action,TaskStatus previousStatus, TaskStatus newStatus){
        ActivityLogRequest request = ActivityLogRequest.builder()
                .user(user).project(project).task(task).previousTaskStatus(previousStatus).currentTaskStatus(newStatus).build();
        eventPublisher.publishEvent(new ActivityLogEvent(this,request));
    }

    //TODO -- did not check authorization
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
    public void  checkProjectPermissions(Project project, ProjectMember projectMember, ProjectRole requiredRole){
        if (project.isStrictMode() && !projectMember.getProjectRole().hasPermission(requiredRole)){
            throw new UnauthorisedException("You do not have permission to perform this action.");
        }
    }



}
