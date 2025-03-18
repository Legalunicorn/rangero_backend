package com.hiroc.rangero.inviteRecord;

import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRecordRepository extends JpaRepository<InviteRecord,Long> {
    Optional<InviteRecord> findByInviteeAndProject(User user, Project project);
}
