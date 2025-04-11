package com.hiroc.rangero.task.helper;


import com.hiroc.rangero.task.Task;
import com.hiroc.rangero.task.dto.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


//USAGE -> in controller

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);
    //map between task DTO and task Entity

    @Mapping(target="assigneeId", source="assignee.id")
    TaskDTO toDto(Task entity);

    //Add custom method for mapping dependeices into Ids?

    default Set<Long> map(Set<Task> dependencies){
        if (dependencies!=null){
            return dependencies.stream().map(Task::getId).collect(Collectors.toSet());
        }
        return new HashSet<Long>();
    }


}
