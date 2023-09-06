package com.everamenkou.springapp.auth.mapper;

import com.everamenkou.springapp.auth.dto.UserDto;
import com.everamenkou.springapp.auth.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto map(UserEntity entity);

    @InheritInverseConfiguration
    UserEntity map(UserDto dto);
}
