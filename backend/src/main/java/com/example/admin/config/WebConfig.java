package com.example.admin.config;

import com.example.admin.auth.interceptor.AdminPermissionInterceptor;
import com.example.admin.auth.interceptor.LoginInterceptor;
import com.example.admin.auth.interceptor.RoleManagePermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 层配置类。
 *
 * 这里用来配置 Spring MVC 的通用能力，比如拦截器、跨域、静态资源等。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 登录拦截器，由 Spring 自动注入。
     */
    private final LoginInterceptor loginInterceptor;

    /**
     * 管理员权限拦截器，用来限制用户管理的写操作。
     */
    private final AdminPermissionInterceptor adminPermissionInterceptor;

    /**
     * 角色管理权限拦截器，用来限制角色管理接口。
     */
    private final RoleManagePermissionInterceptor roleManagePermissionInterceptor;

    public WebConfig(
            LoginInterceptor loginInterceptor,
            AdminPermissionInterceptor adminPermissionInterceptor,
            RoleManagePermissionInterceptor roleManagePermissionInterceptor
    ) {
        this.loginInterceptor = loginInterceptor;
        this.adminPermissionInterceptor = adminPermissionInterceptor;
        this.roleManagePermissionInterceptor = roleManagePermissionInterceptor;
    }

    /**
     * 注册拦截器规则。
     *
     * /api/users 和 /api/users/** 是用户管理接口，需要登录。
     * /api/auth/me 用来查询当前登录用户，也需要登录。
     * /api/auth/logout 用来退出登录，也需要登录。
     * /api/auth/login 没有放进来，所以登录接口本身不会被拦截。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                        "/api/users",
                        "/api/users/**",
                        "/api/roles",
                        "/api/roles/**",
                        "/api/auth/me",
                        "/api/auth/menus",
                        "/api/auth/permissions",
                        "/api/auth/logout"
                );

        registry.addInterceptor(adminPermissionInterceptor)
                .addPathPatterns(
                        "/api/users",
                        "/api/users/**"
                );

        registry.addInterceptor(roleManagePermissionInterceptor)
                .addPathPatterns(
                        "/api/roles",
                        "/api/roles/**"
                );
    }
}
