package com.hiroc.rangero.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TaskRepository  extends JpaRepository<Task,Long> {


    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    Set<Task> findTasksByProjectId(@Param("projectId") Long projectId);


    @Query("SELECT t from Task t WHERE t.id IN :taskIds")
    Set<Task> findAllWithIdsIn(@Param("taskIds") Set<Long> taskIds);


    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.dependencies WHERE task.project.id=:projectId")
    Set<Task> findTaskByProjectIdWithDependencies(@Param("projectId") Long projectId);
}
