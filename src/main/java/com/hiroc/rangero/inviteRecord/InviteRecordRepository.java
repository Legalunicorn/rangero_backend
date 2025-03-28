package com.hiroc.rangero.inviteRecord;

import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface InviteRecordRepository extends JpaRepository<InviteRecord,Long> {
    Optional<InviteRecord> findByInviteeAndProject(User user, Project project);

//    @Query("select iv from InviteRecord iv WHERE iv.user.id=userId")
//    Set<InviteRecord> findUserInvites(@Param("userId") long userId);

    @Query("select iv from InviteRecord iv WHERE iv.invitee.email=:email")
    Set<InviteRecord> findUserInvites(@Param("email") String email);

}
