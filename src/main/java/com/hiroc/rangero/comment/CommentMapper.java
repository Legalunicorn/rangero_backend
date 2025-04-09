package com.hiroc.rangero.comment;


import com.hiroc.rangero.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring",uses = {UserMapper.class})
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target="taskId", source="task.id")
    CommentDTO toDTO(Comment entity);
}
