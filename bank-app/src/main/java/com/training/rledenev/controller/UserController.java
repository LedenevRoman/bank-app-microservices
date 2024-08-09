package com.training.rledenev.controller;

import com.training.rledenev.dto.UserDto;
import com.training.rledenev.entity.enums.Role;
import com.training.rledenev.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveNewClient(@Valid @RequestBody UserDto userDto) {
        return userService.saveNewClient(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserDtoById(@PathVariable(name = "id") Long id) {
        return userService.getUserDtoById(id);
    }

    @GetMapping()
    public UserDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/role")
    public Role getRole() {
        return userService.getAuthorizedUserRole();
    }
}
