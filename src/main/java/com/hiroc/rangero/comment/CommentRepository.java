package com.hiroc.rangero.comment;

import com.hiroc.rangero.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("select c from Comment c WHERE c.task.id=:taskId")
    Set<Comment> findCommentByTaskId(@Param("taskId") Long taskId);

    @Query("select c from Comment c WHERE c.task=:task")
    Set<Comment> findByTask(@Param("task")Task task);
}
