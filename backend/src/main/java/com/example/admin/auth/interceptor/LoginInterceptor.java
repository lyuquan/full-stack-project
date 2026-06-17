package com.example.admin.auth.interceptor;

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
 * 请求进入 Controller 之前，会先经过这里。这里先做学习版 token 校验：
 * 只要请求头 Authorization 的值以 "Bearer study-token-" 开头，就认为已经登录。
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * Authorization 是 HTTP 里常用来放登录凭证的请求头名称。
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer 是 token 常见前缀，后面跟真正的 token 字符串。
     */
    private static final String LEARNING_TOKEN_PREFIX = "Bearer study-token-";

    /**
     * ObjectMapper 用来把 Java 对象转换成 JSON 字符串。
     */
    private final ObjectMapper objectMapper;

    public LoginInterceptor(ObjectMapper objectMapper) {
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
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (authorization != null && authorization.startsWith(LEARNING_TOKEN_PREFIX)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(401, "请先登录")));

        return false;
    }
}
