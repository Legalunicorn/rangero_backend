package com.hiroc.rangero.comment;


import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommentDTO {
    private long id;
    private String body;
    private long taskId;
//    private String fileKey; //S3 Object key ("ie task/1/name.png")
    private String fileName;
    private Long fileSize; //size of file in bytes
}
