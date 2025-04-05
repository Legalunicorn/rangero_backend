package com.hiroc.rangero.inviteRecord;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InviteRecordMapper {

    InviteRecordMapper INSTANCE = Mappers.getMapper(InviteRecordMapper.class);

    @Mapping(target="inviteeEmail",source="invitee.email")
    @Mapping(target="invitorEmail",source="invitor.email")
    @Mapping(target="projectId",source="project.id")
    InviteRecordDTO toDTO(InviteRecord entity);

//    InviteRecord toEntity(InviteRecordDTO dto);
}
