package com.hiroc.rangero.task;


import com.hiroc.rangero.activityLog.ActivityLog;
import com.hiroc.rangero.comment.Comment;
import com.hiroc.rangero.notification.Notification;
import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    //consider m-n
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assignee_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    @OneToMany(mappedBy="task")
    private Set<Comment> comments;

    @OneToMany(mappedBy="task")
    private Set<ActivityLog> taskActivities;

    //TODO consider deleting this if it has no value
    @OneToMany(mappedBy="task")
    private Set<Notification> notifications;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(
            name="task_dependencies",
            joinColumns = @JoinColumn(name="task_id"),
            inverseJoinColumns = @JoinColumn(name ="dependency_id")
    )
    private Set<Task> dependencies = new HashSet<>();

    //Not necessary for now
    //TODO initialize this whe needed
    //@JsonIgnore
    //@ManyToMany(mappedBy="dependencies")
    //private Set<Task> dependents = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdOn;
    @UpdateTimestamp
    private LocalDateTime updatedOn;



}
