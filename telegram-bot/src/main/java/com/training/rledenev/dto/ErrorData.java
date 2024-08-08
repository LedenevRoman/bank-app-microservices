package com.training.rledenev.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorData(HttpStatus httpStatus, LocalDateTime timestamp, String message, String details) {
}
