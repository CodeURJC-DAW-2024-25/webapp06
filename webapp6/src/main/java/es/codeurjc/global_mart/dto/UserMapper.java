package es.codeurjc.global_mart.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import es.codeurjc.global_mart.model.User;

@Mapper (componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);
}