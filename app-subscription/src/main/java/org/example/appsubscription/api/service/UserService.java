package org.example.appsubscription.api.service;

import org.example.appsubscription.api.dto.UserResponseDto;

public interface UserService {
    UserResponseDto findUserByUser(String userName);

    UserResponseDto editSubscriptionTypeUser(String userName, String toSubscriptionType);

    UserResponseDto updateSubscriptionUser(String userName);

    void invalidateExpiredSubscriptions();
}