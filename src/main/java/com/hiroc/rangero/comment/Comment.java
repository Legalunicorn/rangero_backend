package com.hiroc.rangero.comment;


import com.hiroc.rangero.task.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

//Belongs to a comment exclusively
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String body;

    //Attachment
    private String fileKey; //S3 Object key ("ie task/1/name.png")
    private String fileName;
    private Long fileSize; //size of file in bytes

    @ManyToOne
    @JoinColumn(name="task_id")
    private Task task;

    @CreationTimestamp
    private LocalDateTime createdOn;
    @UpdateTimestamp
    private LocalDateTime updatedOn;


}
