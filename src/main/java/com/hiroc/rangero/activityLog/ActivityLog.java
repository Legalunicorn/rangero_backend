package com.hiroc.rangero.activityLog;


import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.enums.TaskStatus;
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

    //one project is to many action
    //one action is to one project
//    @ManyToOne(cascade = CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;


    //one task is to many actions
    // one action for one task
//    @ManyToOne(cascade = CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name ="task_id")
    private Task task;

    //one user -< many actions
    // one action - one user (owner)
//    @ManyToOne(cascade = CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private String assignedEmail;

    @Enumerated(EnumType.STRING)
    private Action action;

    //Nullable
    @Enumerated(EnumType.STRING)
    private TaskStatus previousTaskStatus;
    @Enumerated(EnumType.STRING)
    private TaskStatus currentTaskStatus;

    @CreationTimestamp
    private LocalDateTime createdOn;
}
