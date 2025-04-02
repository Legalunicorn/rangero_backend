package com.hiroc.rangero.notification;


import com.hiroc.rangero.comment.Comment;
import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //one notification has one sender
    // one sender can sender many notifications
    //TODO study the cascading properties properly
    @ManyToOne()
    @JoinColumn(name="sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

//    private Comment comment; -> maybe no need to attack
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    //if assign_task -> we need an assignee // the assignee should be the same as receiver?

    @CreationTimestamp
    private LocalDateTime createdOn;

    private boolean opened; //false means not opened/clicks
    //we also need a way to flip this value if a user opens the comment not via the notification
    // or otherwise we just heck care and only clear it


    //we also need the view status: SEEN, UNSEEN

    //Nullable commentId and taskId
    //from the frontend perspective, we redirect with taskID + commentID
    // if the get page has a comment highlighted
    // how do we load the comment? they are in order
    // i think we just load the task

}
