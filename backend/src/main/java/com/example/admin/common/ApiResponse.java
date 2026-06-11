package com.example.admin.common;

/**
 * 统一接口返回格式。
 *
 * 前端调用后端接口时，最好所有接口都返回类似结构：
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": ...
 * }
 *
 * 这样前端就能用统一逻辑判断请求是否成功、展示错误消息、读取业务数据。
 */
public class ApiResponse<T> {

    /**
     * 业务状态码。
     * 这里先用 200 表示成功，后续登录失败、参数错误等会扩展更多状态码。
     */
    private Integer code;

    /**
     * 给前端看的提示信息。
     */
    private String message;

    /**
     * 真正返回给前端的业务数据。
     * T 是 Java 泛型，表示 data 可以是字符串、对象、列表等不同类型。
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
     * 快速创建成功响应，减少 Controller 里的重复代码。
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(200, "success", data);
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
