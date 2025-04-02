package com.hiroc.rangero.user;


import com.hiroc.rangero.activityLog.ActivityLog;
import com.hiroc.rangero.inviteRecord.InviteRecord;
import com.hiroc.rangero.notification.Notification;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.task.Task;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="_user")
public class User implements UserDetails {

    //TODO define some basic properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //ID

    private String username; //no longer unique
    private String email; //unique
    private String displayName; //can be what
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy="assignee")
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ProjectMember>  projectMembers = new HashSet<>();

    @OneToMany(mappedBy="invitee")
    private Set<InviteRecord> projectInvites = new HashSet<>();

    @OneToMany(mappedBy="user")
    private Set<ActivityLog> userActivities = new HashSet<>();

    @OneToMany(mappedBy="sender")
    private Set<Notification> sentNotifications = new HashSet<>();
    @OneToMany(mappedBy="receiver")
    private Set<Notification> receivedNotifications = new HashSet<>();

    //TODO add helper method in task or in user for adding task and maintaining bidirectional relationship



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword(){
        return password;
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
