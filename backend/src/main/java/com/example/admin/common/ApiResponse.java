package com.example.admin.common;

/**
 * Unified API response wrapper.
 *
 * The frontend can always read code, message and data in the same way,
 * no matter whether the request succeeds or fails.
 */
public class ApiResponse<T> {

    /**
     * Business status code.
     * 200 means success; 400 means invalid request parameters.
     */
    private Integer code;

    /**
     * Message that can be displayed by the frontend.
     */
    private String message;

    /**
     * Real business data returned to the frontend.
     */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Create a success response.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(200, "success", data);
    }

    /**
     * Create an error response.
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<T>(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
