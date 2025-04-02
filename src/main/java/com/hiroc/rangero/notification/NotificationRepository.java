package com.hiroc.rangero.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query("select n from Notification n WHERE n.receiver.email=:email")
    Set<Notification> findAllByReceiverEmail(@Param("email")String email);
}
