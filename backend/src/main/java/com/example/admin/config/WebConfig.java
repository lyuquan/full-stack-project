package com.example.admin.config;

import com.example.admin.auth.interceptor.LoginInterceptor;
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

    public WebConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    /**
     * 注册拦截器规则。
     *
     * addPathPatterns("/api/users", "/api/users/**") 表示 /api/users 本身和它下面的接口都需要先检查登录。
     * /api/auth/login 没有放进来，所以登录接口本身不会被拦截。
     * /api/system/hello 也没有放进来，所以它仍然可以用来测试后端是否启动。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/users", "/api/users/**");
    }
}
