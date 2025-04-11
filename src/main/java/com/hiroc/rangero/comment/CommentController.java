package com.hiroc.rangero.comment;


import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public CommentDTO createComment(@Valid @RequestBody CommentRequestDTO requestDTO){
//        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return commentService.createComment(creator,requestDTO);
//    }

    //TEST - create a file with file upload
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO createComment(@Valid @RequestPart(value="comment",required=true) CommentRequestDTO request,
                                     @RequestPart(value="file",required=false) MultipartFile file) throws IOException {
        User creator  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (file!=null && file.getSize()>8*1000*1000){
            throw new BadRequestException("File size must be less than 8mb.");
        }
        if (file==null && request.getBody().isEmpty()){
            throw new BadRequestException("Empty comments are not allowed");
        }
        return commentService.createComment(creator,request,file);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentService.deleteComment(commentId,user);
    }


}
