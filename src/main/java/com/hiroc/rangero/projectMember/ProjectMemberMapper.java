package com.hiroc.rangero.projectMember;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    ProjectMemberMapper INSTANCE = Mappers.getMapper(ProjectMemberMapper.class);

    @Mapping(source="user.email", target="email")
    @Mapping(source="user.username", target="username")
    @Mapping(source="project.id", target="projectId")
    @Mapping(source="user.id",target="userId")
    ProjectMemberDTO toDTO(ProjectMember entity);
}
