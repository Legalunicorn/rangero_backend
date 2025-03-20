package com.hiroc.rangero.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface TaskRepository  extends JpaRepository<Task,Long> {


    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    Set<Task> findTasksByProjectId(@Param("projectId") Long projectId);
}
