package com.example.admin.auth.service;

import com.example.admin.auth.dto.LoginDTO;
import com.example.admin.auth.vo.LoginVO;
import com.example.admin.common.BusinessException;
import com.example.admin.user.constant.UserConstants;
import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authentication business service.
 *
 * AuthService handles login-related business logic. The Controller only receives
 * requests, while this service decides whether the account can log in.
 */
@Service
public class AuthService {

    /**
     * UserRepository is used to find the user account from the database.
     */
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Login with username and password.
     *
     * This is a learning version: password is compared as plain text.
     * Real projects must compare encrypted password hashes instead.
     */
    public LoginVO login(LoginDTO loginDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(loginDTO.getUsername());

        if (!optionalUser.isPresent()) {
            throw new BusinessException(400, "账号或密码错误");
        }

        UserEntity user = optionalUser.get();

        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new BusinessException(400, "账号或密码错误");
        }

        if (UserConstants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(400, "账号已被禁用");
        }

        return new LoginVO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole()
        );
    }
}
