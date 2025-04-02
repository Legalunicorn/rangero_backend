package com.hiroc.rangero.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@AllArgsConstructor
@Data
public class CommentRequestDTO {
//    @NotNull - cam be nullable if there is an attachment
    private String body;
    @Positive
    private long taskId;

    //Comment can receive notiications
    private Set<String> notifiedUserEmails;
}
