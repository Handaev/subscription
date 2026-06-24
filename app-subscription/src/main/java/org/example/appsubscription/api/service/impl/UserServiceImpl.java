package org.example.appsubscription.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.appsubscription.api.dto.InvalidateSubscriptionUserRecord;
import org.example.appsubscription.api.dto.UserResponseDto;
import org.example.appsubscription.api.entity.SubscriptionType;
import org.example.appsubscription.api.entity.User;
import org.example.appsubscription.api.entity.mapper.UserMapper;
import org.example.appsubscription.api.exception.SubscriptionException;
import org.example.appsubscription.api.exception.SubscriptionNotFoundException;
import org.example.appsubscription.api.repository.UserRepository;
import org.example.appsubscription.api.service.UserService;
import org.example.appsubscription.api.service.Utils.Utils;
import org.example.appsubscription.api.service.kafka.KafkaProducerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.example.appsubscription.api.service.Utils.Utils.setPlusOneMonth;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public UserResponseDto findUserByUser(String userName) {
        User user = userRepository.findById(userName)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(userName);
                    newUser.setSubscriptionType(SubscriptionType.FREE);
                    return userRepository.save(newUser);
                });

        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto editSubscriptionTypeUser(String userName, String toSubscriptionType) {
        SubscriptionType subscriptionType = SubscriptionType.getEnumType(toSubscriptionType);
        if (Objects.isNull(subscriptionType)) {
            throw new SubscriptionException(String.format("Subscription type not found: %s", toSubscriptionType));
        }

        User user = userRepository.findById(userName)
                .orElseThrow(() -> new SubscriptionNotFoundException(String.format("User not found: %s", userName)));

        user.setSubscriptionType(subscriptionType);

        if (SubscriptionType.PAID.equals(subscriptionType) && Objects.isNull(user.getEndDate())) {
            setPlusOneMonth(user);
        }

        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateSubscriptionUser(String userName) {
        User user = userRepository.findById(userName)
                .orElseThrow(() -> new SubscriptionNotFoundException(String.format("User not found: %s", userName)));

        if (SubscriptionType.FREE.equals(user.getSubscriptionType())) {
            throw new SubscriptionException(String.format("User: %s does not have a paid subscription", userName));
        }

        Utils.addOneMonthEndDate(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public void invalidateExpiredSubscriptions() {
        LocalDate currentDate = LocalDate.now();

        List<User> deactivateUsers = userRepository.deactivateSubscription(SubscriptionType.PAID, SubscriptionType.FREE, currentDate);

        List<InvalidateSubscriptionUserRecord> userInvalidateRecords = deactivateUsers.stream()
                .map(user -> new InvalidateSubscriptionUserRecord(user.getName()))
                .toList();

        kafkaProducerService.sendMessageInvalidateCache(userInvalidateRecords);

        log.info("Expired subscriptions invalidated. User count: {}", deactivateUsers.size());
    }
}