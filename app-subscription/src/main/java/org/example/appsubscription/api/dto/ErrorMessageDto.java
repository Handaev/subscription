package org.example.appsubscription.api.dto;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Builder
public class ErrorMessageDto {

    private Integer statusCode;

    private String message;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime timestamp;
}