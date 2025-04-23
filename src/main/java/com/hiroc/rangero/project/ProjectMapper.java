package com.hiroc.rangero.project;

import com.hiroc.rangero.project.dto.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "creatorEmail",source = "creator.email")
    @Mapping(target = "creatorUsername", source ="creator.username")
    ProjectDTO toDTO(Project entity);

}
