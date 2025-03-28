package com.hiroc.rangero.activityLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {

    @Query("select log from ActivityLog log WHERE log.project.id=:projectId")
    Set<ActivityLog> findByProjectId(@Param("projectId") Long projectId);
}
