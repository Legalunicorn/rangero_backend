package com.hiroc.rangero.projectMember;

import com.hiroc.rangero.project.Project;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ProjectMemberRepository  extends JpaRepository<ProjectMember,Long> {

    @Query("select pm from ProjectMember pm WHERE pm.project.id=:projectId")
    Set<ProjectMember> findAllProjectMembers(@Param("projectId") Long projectId);

    Optional<ProjectMember> findByUserAndProject(User user, Project project);

    @Query("""
            select pm from ProjectMember pm 
            WHERE pm.user.email=:email AND pm.project.id=:projectId
           """)
    Optional<ProjectMember> findByUserEmailAndProjectId(@Param("email")String email,@Param("projectId") long projectId);

    @Query("select pm from ProjectMember pm where pm.user.id=:userId AND pm.project.id=:projectId")
    Optional<ProjectMember> findByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);


    @Query("""
            select pm from ProjectMember pm
            WHERE pm.user.email IN :email AND pm.project=:project
            """)
    Set<ProjectMember> findByUserEmailsAndProject(@Param("project") Project project, @Param("email") Set<String> emails);

//    @Query("""
//            SELECT pm FROM ProjectMember pm
//            WHERE pm.user.id=:userId AND pm.project.id=:projectId
//            """)
//    Optional<ProjectMember> findByUserIdAndProjectId(@Param("userId") long userId, @Param("projectId") Long projectId);
//
//    @Query("""
//           select pm from ProjectMember pm
//           where pm.user.email=:userEmail and pm.project.id=:projectId
//           """)
//    Optional<ProjectMember> findByUserEmailAndProjectId(@Param("userEmail") String userEmail,@Param("projectId") long projectId);
}
