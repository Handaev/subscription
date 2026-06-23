package org.example.appsubscription.api.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.appsubscription.api.controller.UserV1Api;
import org.example.appsubscription.api.dto.UserResponseDto;
import org.example.appsubscription.api.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerV1 implements UserV1Api {

    private  final UserServiceImpl userService;

    @Override
    public ResponseEntity<UserResponseDto> getUserByName(String userName) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserByUser(userName));
    }

    @Override
    public ResponseEntity<UserResponseDto> editTypeSubscriptionForUser(String userName, String toSubscriptionType) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.editSubscriptionTypeUser(userName, toSubscriptionType));
    }

    @Override
    public ResponseEntity<UserResponseDto> updateSubscriptionForUser(String userName) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateSubscriptionUser(userName));
    }
}