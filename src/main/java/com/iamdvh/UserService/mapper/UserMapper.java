package com.iamdvh.UserService.mapper;

import com.iamdvh.UserService.dto.request.UserRequest;
import com.iamdvh.UserService.dto.response.UserResponse;
import com.iamdvh.UserService.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(UserEntity entity);
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "dob", ignore = true)
    UserEntity toEntity(UserRequest request);
    UserEntity toEntity(@MappingTarget UserEntity entity, UserRequest request);

}
