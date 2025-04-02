package com.hiroc.rangero.notification;

import com.hiroc.rangero.notification.dto.NotificationDTO;
import com.hiroc.rangero.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",uses = UserMapper.class)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    //map request to entity
    //Not working anymore our request is not relaated to the entity directly
//    Notification toEntity(NotificationRequest request);
    @Mapping(target="taskId",source="task.id")
    NotificationDTO toDto(Notification entity);
}
