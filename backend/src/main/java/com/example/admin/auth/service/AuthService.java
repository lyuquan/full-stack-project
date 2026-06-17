package com.example.admin.auth.service;

import com.example.admin.auth.dto.LoginDTO;
import com.example.admin.auth.vo.LoginVO;
import com.example.admin.common.BusinessException;
import com.example.admin.user.constant.UserConstants;
import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
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

    /**
     * BCrypt 密码工具，用来校验用户输入的明文密码和数据库里的密文是否匹配。
     */
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Login with username and password.
     *
     * 前端传过来的是明文密码，数据库保存的是 BCrypt 密文。
     * 所以这里不能再用 equals，而要用 passwordEncoder.matches 做安全校验。
     */
    public LoginVO login(LoginDTO loginDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(loginDTO.getUsername());

        if (!optionalUser.isPresent()) {
            throw new BusinessException(400, "账号或密码错误");
        }

        UserEntity user = optionalUser.get();

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "账号或密码错误");
        }

        if (UserConstants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(400, "账号已被禁用");
        }

        return new LoginVO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole(),
                createLearningToken(user)
        );
    }

    /**
     * 生成学习版 token。
     *
     * 这个 token 只用来帮助理解登录流程。真实项目通常会使用 JWT
     * 这种带签名的 token，或者使用服务端 session 保存登录状态。
     */
    private String createLearningToken(UserEntity user) {
        return "study-token-" + user.getId() + "-" + UUID.randomUUID().toString();
    }
}
