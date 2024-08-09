package com.training.rledenev.controller;

import com.training.rledenev.dto.UserDto;
import com.training.rledenev.entity.User;
import com.training.rledenev.security.jwt.JwtProvider;
import com.training.rledenev.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/auth")
public class AuthController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public String auth(@RequestBody UserDto userDto) {
        User user = userService.findByEmailAndPassword(userDto.getEmail(), userDto.getPassword());
        return jwtProvider.generateToken(user.getEmail());
    }

}
