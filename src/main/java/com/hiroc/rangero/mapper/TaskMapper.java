package com.hiroc.rangero.mapper;


import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


//USAGE -> in controller

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);
    //map between task DTO and task Entity

    @Mapping(target="assigneeId", source="assignee.id")
    TaskDTO toDto(Task entity);

//    @Mapping(target="assignee.id", source="assigneeId")
//    Task toEntity(TaskDTO dto);


}
