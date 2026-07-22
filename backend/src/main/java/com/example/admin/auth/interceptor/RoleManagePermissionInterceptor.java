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
 * Role management permission interceptor.
 *
 * Login only proves "who you are". This interceptor checks whether the current
 * login user owns role:manage before entering role management APIs.
 */
@Component
public class RoleManagePermissionInterceptor implements HandlerInterceptor {

    /**
     * Token service finds the current login user from the Authorization header.
     */
    private final AuthTokenService authTokenService;

    /**
     * ObjectMapper converts the unified response object to JSON.
     */
    private final ObjectMapper objectMapper;

    public RoleManagePermissionInterceptor(AuthTokenService authTokenService, ObjectMapper objectMapper) {
        this.authTokenService = authTokenService;
        this.objectMapper = objectMapper;
    }

    /**
     * Runs before RoleController.
     *
     * Only users with role:manage can query, create, edit, delete or assign
     * role permissions.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AuthTokenService.LoginUser loginUser = authTokenService.getLoginUser(request);

        if (loginUser != null && AuthPermissions.canManageRoles(loginUser.getPermissions())) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(403, "没有角色管理权限")));

        return false;
    }
}
