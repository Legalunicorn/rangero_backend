package com.hiroc.rangero.activityLog;


import com.fasterxml.jackson.annotation.JacksonInject;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskStatus;
import com.hiroc.rangero.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //TODO update project entity + mapper
    //one project is to many action
    //one action is to one project
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private Project project;


    //TODO update task entity + mapper
    //one task is to many actions
    // one action for one task
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="task_id")
    private Task task;

    //TODO update user entity + mappter
    //one user -< many actions
    // one action - one user (owner)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;


    private Action action;

    //Nullable
    private TaskStatus previousTaskStatus;
    private TaskStatus currentTaskStatus;

    @CreationTimestamp
    private LocalDateTime createdOn;
}
