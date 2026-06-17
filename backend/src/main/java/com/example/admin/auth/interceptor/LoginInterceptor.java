package com.example.admin.auth.interceptor;

import com.example.admin.auth.service.AuthTokenService;
import com.example.admin.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 登录拦截器。
 *
 * 请求进入 Controller 之前，会先经过这里。
 * 这里读取 Authorization 请求头，并检查 token 是否真的存在于后端的 token 仓库。
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 学习版 token 服务，用来判断 token 是否由后端登录接口签发过。
     */
    private final AuthTokenService authTokenService;

    /**
     * ObjectMapper 用来把 Java 对象转换成 JSON 字符串。
     */
    private final ObjectMapper objectMapper;

    public LoginInterceptor(AuthTokenService authTokenService, ObjectMapper objectMapper) {
        this.authTokenService = authTokenService;
        this.objectMapper = objectMapper;
    }

    /**
     * preHandle 会在 Controller 方法执行前触发。
     *
     * 返回 true：继续执行 Controller。
     * 返回 false：拦截请求，Controller 不会继续执行。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = authTokenService.getToken(request);

        if (token != null && authTokenService.isValid(token)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(401, "请先登录")));

        return false;
    }
}
