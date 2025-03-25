package com.hiroc.rangero.project;


import com.hiroc.rangero.activityLog.ActivityLog;
import com.hiroc.rangero.inviteRecord.InviteRecord;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Project {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    private String name;
    private boolean strictMode;
    //TODO map one project to many tasks

    @ManyToOne
    @NotNull
    private User creator;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> projectMembers;

    @OneToMany(mappedBy = "project")
    private Set<InviteRecord> projectInvites;

    @OneToMany(mappedBy = "project")
    private Set<ActivityLog> projectActivities;

    @OneToMany(mappedBy="project")
    private Set<Task> tasks;


    @CreationTimestamp
    private LocalDateTime createdOn;
    @UpdateTimestamp
    private LocalDateTime updatedOn;

    //Helper method to save new member
    public void addProjectMember(ProjectMember member){
        if (this.projectMembers==null) this.projectMembers = new HashSet<>();
        this.projectMembers.add(member);
        member.setProject(this);
    }
}
