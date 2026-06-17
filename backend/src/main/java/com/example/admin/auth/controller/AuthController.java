package com.example.admin.auth.controller;

import com.example.admin.auth.dto.LoginDTO;
import com.example.admin.auth.service.AuthService;
import com.example.admin.auth.service.AuthTokenService;
import com.example.admin.auth.vo.LoginVO;
import com.example.admin.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证接口入口。
 *
 * Controller 负责接收登录、查询当前登录用户、退出登录请求。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 登录业务服务，负责账号密码校验和生成 token。
     */
    private final AuthService authService;

    /**
     * token 服务，负责根据请求里的 token 查询用户或删除 token。
     */
    private final AuthTokenService authTokenService;

    public AuthController(AuthService authService, AuthTokenService authTokenService) {
        this.authService = authService;
        this.authTokenService = authTokenService;
    }

    /**
     * 登录接口。
     *
     * 示例：POST /api/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginUser = authService.login(loginDTO);

        return ApiResponse.success(loginUser);
    }

    /**
     * 查询当前登录用户。
     *
     * 示例：GET /api/auth/me
     */
    @GetMapping("/me")
    public ApiResponse<AuthTokenService.LoginUser> me(HttpServletRequest request) {
        AuthTokenService.LoginUser loginUser = authTokenService.getLoginUser(request);

        if (loginUser == null) {
            return ApiResponse.error(401, "请先登录");
        }

        return ApiResponse.success(loginUser);
    }

    /**
     * 退出登录。
     *
     * 示例：POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        authTokenService.removeToken(request);

        return ApiResponse.success(null);
    }
}
