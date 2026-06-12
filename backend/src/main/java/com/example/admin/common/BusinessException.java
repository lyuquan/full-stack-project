package com.example.admin.common;

/**
 * Business exception for rules that are not simple parameter format checks.
 *
 * Example: username already exists. The request format is valid, but the
 * business rule does not allow saving it.
 */
public class BusinessException extends RuntimeException {

    /**
     * Business status code returned to the frontend.
     */
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
