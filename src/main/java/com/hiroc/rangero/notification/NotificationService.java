package com.hiroc.rangero.notification;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    //Create a notification by an event


    @TransactionalEventListener
    @Async
    public void createMentionNotification(NotificationEvent event){
        NotificationRequest request = event.getRequest();
        Notification notification = notificationMapper.toEntity(request);
        notification.setOpened(false);
        notificationRepository.save(notification);
    }
}
