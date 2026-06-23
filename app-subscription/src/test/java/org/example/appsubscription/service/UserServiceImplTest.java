package org.example.appsubscription.service;

import org.example.appsubscription.api.dto.InvalidateSubscriptionUserRecord;
import org.example.appsubscription.api.dto.UserResponseDto;
import org.example.appsubscription.api.entity.SubscriptionType;
import org.example.appsubscription.api.entity.User;
import org.example.appsubscription.api.entity.mapper.UserMapper;
import org.example.appsubscription.api.exception.SubscriptionException;
import org.example.appsubscription.api.exception.SubscriptionNotFoundException;
import org.example.appsubscription.api.repository.UserRepository;
import org.example.appsubscription.api.service.impl.UserServiceImpl;
import org.example.appsubscription.api.service.kafka.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findUserByUser_WhenUserExists_ShouldReturnUserDto() {
        String username = "test_user";
        User user = new User();
        user.setName(username);
        UserResponseDto expectedDto = new UserResponseDto();

        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(expectedDto);

        UserResponseDto result = userService.findUserByUser(username);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserByUser_WhenUserDoesNotExist_ShouldCreateAndReturnNewUserDto() {
        String username = "new_user";
        UserResponseDto expectedDto = new UserResponseDto();

        when(userRepository.findById(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(expectedDto);

        UserResponseDto result = userService.findUserByUser(username);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void editSubscriptionTypeUser_WhenTypeInvalid_ShouldThrowSubscriptionException() {
        String username = "test_user";
        String invalidType = "INVALID_TYPE";

        assertThrows(SubscriptionException.class, () ->
                userService.editSubscriptionTypeUser(username, invalidType)
        );
        verify(userRepository, never()).findById(any());
    }

    @Test
    void editSubscriptionTypeUser_WhenUserNotFound_ShouldThrowSubscriptionNotFoundException() {
        String username = "unknown_user";
        String targetType = "PAID";

        when(userRepository.findById(username)).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () ->
                userService.editSubscriptionTypeUser(username, targetType)
        );
    }

    @Test
    void editSubscriptionTypeUser_ToPaidAndEndDateNull_ShouldSetEndDateAndReturnDto() {
        String username = "test_user";
        String targetType = "PAID";
        User user = new User();
        user.setName(username);
        user.setSubscriptionType(SubscriptionType.FREE);
        user.setEndDate(null);
        UserResponseDto expectedDto = new UserResponseDto();

        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(expectedDto);

        UserResponseDto result = userService.editSubscriptionTypeUser(username, targetType);

        assertEquals(result, expectedDto);
    }

    @Test
    void updateSubscriptionUser_WhenUserNotFound_ShouldThrowSubscriptionNotFoundException() {
        String username = "unknown_user";

        when(userRepository.findById(username)).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () ->
                userService.updateSubscriptionUser(username)
        );
    }

    @Test
    void updateSubscriptionUser_WhenUserHasFreeSubscription_ShouldThrowSubscriptionException() {
        String username = "free_user";
        User user = new User();
        user.setName(username);
        user.setSubscriptionType(SubscriptionType.FREE);

        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        assertThrows(SubscriptionException.class, () ->
                userService.updateSubscriptionUser(username)
        );
    }

    @Test
    void updateSubscriptionUser_WhenUserHasPaidSubscription_ShouldExtendSubscriptionAndReturnDto() {
        String username = "paid_user";
        User user = new User();
        user.setName(username);
        user.setSubscriptionType(SubscriptionType.PAID);
        LocalDate initialEndDate = LocalDate.now().plusDays(5);
        user.setEndDate(initialEndDate);
        UserResponseDto expectedDto = new UserResponseDto();

        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(expectedDto);

        UserResponseDto result = userService.updateSubscriptionUser(username);

        assertNotNull(result);
        assertTrue(user.getEndDate().isAfter(initialEndDate));
        verify(userMapper).toUserResponseDto(user);
    }

    @Test
    void invalidateExpiredSubscriptions_WhenUsersExpired_ShouldSendDeactivatedUsersToKafka() {
        User user1 = new User();
        user1.setName("user1");
        User user2 = new User();
        user2.setName("user2");
        List<User> expiredUsers = List.of(user1, user2);

        when(userRepository.deactivateSubscription(eq(SubscriptionType.PAID), eq(SubscriptionType.FREE), any(LocalDate.class)))
                .thenReturn(expiredUsers);

        userService.invalidateExpiredSubscriptions();

        List<InvalidateSubscriptionUserRecord> expectedRecords = List.of(
                new InvalidateSubscriptionUserRecord("user1"),
                new InvalidateSubscriptionUserRecord("user2")
        );
        verify(kafkaProducerService, times(1)).sendMessageInvalidateCache(expectedRecords);
    }
}
