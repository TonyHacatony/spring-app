package com.everamenkou.springapp.rest;

import com.everamenkou.springapp.auth.dto.AuthRequestDto;
import com.everamenkou.springapp.auth.dto.AuthResponseDto;
import com.everamenkou.springapp.auth.dto.UserDto;
import com.everamenkou.springapp.auth.entity.UserEntity;
import com.everamenkou.springapp.auth.mapper.UserMapper;
import com.everamenkou.springapp.auth.security.CustomPrincipal;
import com.everamenkou.springapp.auth.security.SecurityService;
import com.everamenkou.springapp.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        UserEntity entity = userMapper.map(dto);
        return userService.registerUser(entity).map(userMapper::map);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        //TODO maybe mapstruct to AuthReq -> TokenInfo
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                        .build()));
    }

    @GetMapping("/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getUserById(customPrincipal.getId())
                .map(userMapper::map);
    }
}
