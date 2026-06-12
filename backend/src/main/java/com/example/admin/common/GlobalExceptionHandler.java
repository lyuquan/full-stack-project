package com.example.admin.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for all controllers.
 *
 * Without this class, validation errors would be returned by Spring Boot's
 * default error format. With this class, validation errors also follow our
 * ApiResponse format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors triggered by @Valid.
     *
     * Example: username is blank -> returns { code: 400, message: "账号不能为空" }.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        return ApiResponse.error(400, message);
    }

    /**
     * Handle business rule errors thrown by Service.
     *
     * Example: username already exists -> returns { code: 400, message: "账号已存在" }.
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.error(exception.getCode(), exception.getMessage());
    }
}
