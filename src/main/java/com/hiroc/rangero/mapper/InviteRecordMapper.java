package com.hiroc.rangero.mapper;

import com.hiroc.rangero.inviteRecord.InviteRecord;
import com.hiroc.rangero.inviteRecord.InviteRecordDTO;
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
