package com.hiroc.rangero.task;


import com.hiroc.rangero.activityLog.Action;
import com.hiroc.rangero.activityLog.ActivityLogEvent;
import com.hiroc.rangero.activityLog.ActivityLogRequest;
import com.hiroc.rangero.email.EmailEvent;
import com.hiroc.rangero.email.EmailService;
import com.hiroc.rangero.email.dto.EmailRequest;
import com.hiroc.rangero.email.enums.EmailType;
import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.TaskNotFoundException;
import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.notification.NotificationEvent;
import com.hiroc.rangero.notification.NotificationRepository;
import com.hiroc.rangero.notification.dto.NotificationRequest;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.project.ProjectService;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.projectMember.ProjectRole;
import com.hiroc.rangero.task.dto.TaskDTO;
import com.hiroc.rangero.task.dto.TaskRequestDTO;
import com.hiroc.rangero.task.enums.TaskStatus;
import com.hiroc.rangero.task.helper.CycleDetection;
import com.hiroc.rangero.task.helper.TaskMapper;
import com.hiroc.rangero.user.User;
import com.hiroc.rangero.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
            modifyTaskAssigneeWithLoggingAndNotification(user,request.getAssigneeEmail(),project,newTask);
        } else{
            log.info(">>> request has no assignee email");
        }
        taskRepository.save(newTask);

        //TODO, should this be sync or async, can newTask be used or not
        createActivityLog(newTask,user,Action.CREATE_TASK);
        log.debug("Task created");

        return newTask;

    }

    @Transactional
    //Normal  fields : Title, Due Date, Priority
    //Tricky fields : Status, Assignee, Dependencies
    public TaskDTO patchTaskDetails(User user, Long taskId, TaskRequestDTO updatedDetails){

        //############## VALIDATION ######################
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new BadRequestException("Task with id " + taskId + " does not exist."));
        ProjectMember member = projectMemberService.findByUserAndProject(user,task.getProject())
                .orElseThrow(UnauthorisedException::new);
        //If strict mode enabled, only ADMINS, OWNERS can update tasks
        checkProjectPermissions(task.getProject(),member,ProjectRole.ADMIN);

        // ############### UPDATING LOGIC #################
        //trivial: title, priority, due_date
        //No need action pa
        if (updatedDetails.getTitle()!=null) task.setTitle(updatedDetails.getTitle());
        if (updatedDetails.getDueDate()!=null) task.setDueDate(updatedDetails.getDueDate());
        if (updatedDetails.getPriority()!=null) task.setPriority(updatedDetails.getPriority());

        //Harder: status, assignee, dependencies adding
        if (updatedDetails.getStatus()!=null && task.getStatus()!=updatedDetails.getStatus()) {
            if (updatedDetails.getStatus()==TaskStatus.COMPLETED){
                //Check that dependencies are completed first
                Set<Task> dependencies = task.getDependencies();
                for (Task dependency : dependencies){
                    if (dependency.getStatus()!=TaskStatus.COMPLETED){
                        throw new BadRequestException("Task: "+dependency.getTitle()+" is a dependency and must be completed before this task is completed");
                    }
                }
            }
            createActivityLog(task,user,Action.UPDATE_TASK_DETAILS);
            task.setStatus(updatedDetails.getStatus());
        }
        if (updatedDetails.getAssigneeEmail()!=null){
            modifyTaskAssigneeWithLoggingAndNotification(user,updatedDetails.getAssigneeEmail(),task.getProject(),task);
        }
        //task dependencies should by default be empty in the request
        if (updatedDetails.getTaskDependencies()!=null && !updatedDetails.getTaskDependencies().isEmpty()){
            addTaskDependencies(user,task,updatedDetails.getTaskDependencies());
            //TODO don't trigger this accidentally though
            createActivityLog(task,user,Action.UPDATE_TASK_DEPENDENCIES);
        }

        log.info("Task with ID {} updated by user {}", taskId, user.getUsername());
        return taskMapper.toDto(task);

    }

    //TODO => verify that this should have its own dedicated endpoint
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

        //verify that the dependencies of the task are cleared if were to set to completed
        if (newStatus==TaskStatus.COMPLETED){
            for (Task dependency: task.getDependencies()){
                if (dependency.getStatus()!=TaskStatus.COMPLETED){
                    throw new BadRequestException("Task: "+dependency.getTitle()+" is a dependency and must be completed before this task is completed");
                }
            }
        }

        //3. modify the task and commit
        createActivityLog(task,user,newStatus);
        task.setStatus(newStatus);
        taskRepository.save(task);
        return task;
    }

    @Transactional
    public void addTaskDependencies(User user, Task task, Set<Long> taskDependencyIds){
        // We extract those that can be found. If not found => No error thrown + nothing changes
        Project project = task.getProject();
        Set<Task> taskDependencies = taskRepository.findAllWithIdsIn(taskDependencyIds); //we add these task one by one
        //
        //Make sure these tasks belong to the project
        for (Task t: taskDependencies) {
            if (t.getProject().getId()!=project.getId()){
                throw new UnauthorisedException("Unable to modify task from another project. Task ID:"+t.getId());
            }
        }

        //1 - Cycle detection itself once data is validated
        Set<Task> allProjectTask = taskRepository.findTaskByProjectIdWithDependencies(project.getId());
        Map<Long,Set<Long>> adjacencyList = new HashMap<>(); //TaskID -> to its dependencies
        for(Task t: allProjectTask){
            Set<Long> dependencyIds = t.getDependencies().stream().map(Task::getId).collect(Collectors.toSet());
            adjacencyList.put(t.getId(),dependencyIds);
        }

        for (Task newDependency: taskDependencies){
            if (adjacencyList.get(task.getId()).contains(newDependency.getId())){
                //Relationship is already established
                continue;
            }
            adjacencyList.get(task.getId()).add(newDependency.getId());
            CycleDetection.detectCycle(adjacencyList,task.getId());
            task.addDependency(newDependency);
        }
    }
    // HELPER METHODS #################################################################
    //TODO consider refactor helper methods to their own class

    //No status change
    private void createActivityLog(Task task, User user, Action action){
        ActivityLogRequest request = ActivityLogRequest.builder()
                .user(user).project(task.getProject()).task(task).action(action).build();
        eventPublisher.publishEvent(new ActivityLogEvent(this,request));
    }
    private void createActivityLog(Task task,User user ,TaskStatus newStatus){
        ActivityLogRequest request = ActivityLogRequest.builder()
                .user(user).project(task.getProject()).task(task).previousTaskStatus(task.getStatus()).currentTaskStatus(newStatus).action(Action.UPDATE_TASK_STATUS).build();
        eventPublisher.publishEvent(new ActivityLogEvent(this,request));
    }

    //TODO -- did not check authorization

    private void modifyTaskAssigneeWithLoggingAndNotification(User admin,String newAssigneeEmail, Project project, Task task){
        User newAssignee = userService.findByEmail(newAssigneeEmail);
        if (newAssignee==null) throw new BadRequestException(">>> User with email "+newAssigneeEmail+" does not exist");
        projectMemberService.findByUserAndProject(newAssignee,project)
                .orElseThrow(()->new BadRequestException(">>> User with email "+newAssigneeEmail+" is not a member of this project"));
        log.info(">>> assigning task to: {}", newAssigneeEmail);
        task.setAssignee(newAssignee);

        //Notifications
        // - In App notification
        // - Project acitivty log
        // - Email assigned user
        notifyAssignee(admin,newAssignee,task);
        createActivityLog(task,admin,Action.ASSIGN_TASK);
        emailAssignee(admin,newAssignee,task,project.getName());



    }

    private void notifyAssignee(User admin, User assignee, Task task){
        NotificationRequest request = NotificationRequest.builder()
                .sender(admin)
                .validUsers(Set.of(assignee))
                .task(task)
                .build();
        eventPublisher.publishEvent(new NotificationEvent(this, request));
    }


    //Notify assignee by email
    private void emailAssignee(User admin, User assignee, Task task,String projectName){
        if (!assignee.getSettings().isAssignmentEmailEnabled()) return; //do not email user

        EmailRequest request = EmailRequest.builder()
                .body("""
                        Dear %s,
                        
                        %s has assigned you to the task "%s".
                        If you do not wish to receive such emails, please disable them in your account settings.
                        
                        Cheers,
                        Rangero Team
                        """.formatted(assignee.getUsername(),admin.getUsername(),task.getTitle()))
                .subject("Task Assignment - "+projectName)
                .recipient(assignee.getEmail())
                .build();

        eventPublisher.publishEvent(new EmailEvent(this,request));

    }

    //TODO - resolve: both project and project member must be fetched every time
    //consider creating this as a bean for flexibility or just manually checking on a case to case basis
    public void  checkProjectPermissions(Project project, ProjectMember projectMember, ProjectRole requiredRole){
        if (project.isStrictMode() && !projectMember.getProjectRole().hasPermission(requiredRole)){
            throw new UnauthorisedException("You do not have permission to perform this action.");
        }
    }

}
