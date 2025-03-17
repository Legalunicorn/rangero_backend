package com.hiroc.rangero.inviteRecord;


import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InviteRecord {
    @id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private InviteStatus inviteStatus;

    //map to a single group?

    //many invites to one user
    @ManyToOne
    @JoinColumn(name = "invitee_id")
    private User invitee;

    //one invite record -> belongs to one project
    // one project -> has many associated invite records
    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;


}
