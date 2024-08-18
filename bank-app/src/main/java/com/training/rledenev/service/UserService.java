package com.training.rledenev.service;

import com.training.rledenev.dto.UserDto;
import com.training.rledenev.entity.User;
import com.training.rledenev.enums.Role;

public interface UserService {
    UserDto saveNewClient(UserDto userDto);

    User findByEmailAndPassword(String email, String password);

    Role getAuthorizedUserRole();

    UserDto getUserDtoById(Long id);

    UserDto getCurrentUser();
}
