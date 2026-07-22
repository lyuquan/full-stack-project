package com.example.admin.auth.interceptor;

import com.example.admin.auth.constant.AuthPermissions;
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
 * 管理员权限拦截器。
 *
 * 登录拦截器只负责判断“有没有登录”，这里负责判断“有没有权限操作用户数据”。
 * 本项目先规定：只有超级管理员可以新增、编辑、启用禁用、删除用户。
 */
@Component
public class AdminPermissionInterceptor implements HandlerInterceptor {

    /**
     * token 服务可以根据请求头里的 token 找到当前登录用户。
     */
    private final AuthTokenService authTokenService;

    /**
     * ObjectMapper 用来把统一返回对象转换成 JSON 字符串。
     */
    private final ObjectMapper objectMapper;

    public AdminPermissionInterceptor(AuthTokenService authTokenService, ObjectMapper objectMapper) {
        this.authTokenService = authTokenService;
        this.objectMapper = objectMapper;
    }

    /**
     * Controller 执行前先进入这里。
     *
     * GET 是查询操作，登录用户都可以访问。
     * POST、PUT、PATCH、DELETE 是写操作，只有超级管理员可以访问。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isReadRequest(request)) {
            return true;
        }

        AuthTokenService.LoginUser loginUser = authTokenService.getLoginUser(request);

        // Check the permission list instead of checking role text directly.
        // This makes the rule easier to extend when more roles can own user:write later.
        if (loginUser != null && AuthPermissions.canManageUsers(loginUser.getPermissions())) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(403, "没有操作权限")));

        return false;
    }

    /**
     * 判断当前请求是不是查询请求。
     *
     * equalsIgnoreCase 表示忽略大小写比较，避免 get / GET 这种大小写差异造成误判。
     */
    private boolean isReadRequest(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }
}
