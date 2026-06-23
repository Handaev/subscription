package org.example.appsubscription.api.entity.mapper;

import org.example.appsubscription.api.dto.UserResponseDto;
import org.example.appsubscription.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);
}