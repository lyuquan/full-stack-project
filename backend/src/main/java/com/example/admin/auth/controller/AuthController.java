package com.example.admin.auth.controller;

import com.example.admin.auth.dto.LoginDTO;
import com.example.admin.auth.service.AuthService;
import com.example.admin.auth.vo.LoginVO;
import com.example.admin.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Authentication API entry.
 *
 * Controller receives login requests from the frontend and delegates business
 * checks to AuthService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Authentication business service injected by Spring.
     */
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login API.
     *
     * Example: POST /api/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginUser = authService.login(loginDTO);

        return ApiResponse.success(loginUser);
    }
}
