package com.hiroc.rangero.activityLog;

import com.hiroc.rangero.activityLog.dto.ActivityLogDTO;
import com.hiroc.rangero.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ActivityLogMapper {
    ActivityLogMapper INSTANCE = Mappers.getMapper(ActivityLogMapper.class);

    //Since we use UserMapper, the property should be interpreted automatically
    @Mapping(target="projectId", source="project.id")
    @Mapping(target="taskId", source="task.id")
    ActivityLogDTO toDTO(ActivityLog entity);

    ActivityLog requestToEntity(ActivityLogRequest dto);
}
