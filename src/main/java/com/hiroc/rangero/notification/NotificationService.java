package com.hiroc.rangero.notification;


import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.notification.dto.NotificationDTO;
import com.hiroc.rangero.notification.dto.NotificationRequest;

import java.util.Set;

import com.hiroc.rangero.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    //Create a notification by an event

    public Set<NotificationDTO> getUserNotifications(User accessor,String email){
        if (!accessor.getEmail().equals(email)) throw new UnauthorisedException();
        return notificationRepository.findAllByReceiverEmail(email).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toSet());
    }


    @TransactionalEventListener
    @Async
    public void createNotification(NotificationEvent event){
        NotificationRequest request = event.getRequest();
        Set<Notification>  notifications = request.getValidUsers().stream().map(user -> {
            return Notification.builder()
                    .notificationType(request.getNotificationType())
                    .sender(request.getSender())
                    .receiver(user)
                    .task(request.getTask())
                    .opened(false)
                    .build();
        }).collect(Collectors.toSet());
        notificationRepository.saveAll(notifications);

    }

    //for comment notification
}
