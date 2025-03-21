package com.hiroc.rangero.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class CommentRequestDTO {
    @NotNull
    private String body;
    @Positive
    private long taskId;
}
