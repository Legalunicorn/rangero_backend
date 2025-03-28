package com.hiroc.rangero.user;


import com.hiroc.rangero.user.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


// USED BY :
// - ActivityLogMapper
// - UserInvite
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target="userId", source="id")
    UserDTO toDTO(User entity);
}
