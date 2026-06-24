package org.example.appsubscription.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.example.appsubscription.api.dto.ErrorMessageDto;
import org.example.appsubscription.api.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "Управление пользователями и их подписками")
public interface UserV1Api {

    @Operation(
        operationId = "getUserByName",
        summary = "Получить пользователя по имени",
        tags = {"User"},
        responses = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно получен", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Введены не корректные данные", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))
            })
        }
    )
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{user_name}",
            produces = { "application/json" }
    )
    ResponseEntity<UserResponseDto> getUserByName(
            @NotNull @Parameter(name = "user_name", description = "Имя пользователя", required = true, in = ParameterIn.PATH) @PathVariable("user_name") String userName
    );

    @Operation(
            operationId = "editTypeSubscriptionForUser",
            summary = "Изменить тип подписки пользователя",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Тип успешно изменен", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Введены не корректные данные", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/{user_name}/type/{subscription_type}",
            produces = { "application/json" }
    )
    ResponseEntity<UserResponseDto> editTypeSubscriptionForUser(
            @NotNull @Parameter(name = "user_name", description = "Имя пользователя", required = true, in = ParameterIn.PATH) @PathVariable("user_name") String userName,
            @NotNull @Parameter(name = "subscription_type", description = "Целевой тип подписки", required = true, in = ParameterIn.PATH)
            @PathVariable("subscription_type") String toSubscriptionType
    );

    @Operation(
            operationId = "updateSubscriptionForUser",
            summary = "Обновить время подписки для пользователя",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Подписка обновлена", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Введены не корректные данные", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/{user_name}",
            produces = { "application/json" }
    )
    ResponseEntity<UserResponseDto> updateSubscriptionForUser(
            @NotNull @Parameter(name = "user_name", description = "Имя пользователя", required = true, in = ParameterIn.PATH) @PathVariable("user_name") String userName
    );
}