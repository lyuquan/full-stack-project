package com.example.admin.user.config;

import com.example.admin.user.constant.UserConstants;
import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Initializes demo users when the database is empty.
 *
 * CommandLineRunner runs after Spring Boot starts. We use it to prepare the
 * first three users only once, so later edits and deletes can stay in the H2 file database.
 */
@Component
@Order(2)
public class UserDataInitializer implements CommandLineRunner {

    /**
     * Repository used to check and insert demo users.
     */
    private final UserRepository userRepository;

    /**
     * BCrypt 密码工具，用来把演示用户的默认密码保存成密文。
     */
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            upgradePlainTextPasswords();
            return;
        }

        userRepository.saveAll(Arrays.asList(
                createUser("admin", "系统管理员", UserConstants.ROLE_SUPER_ADMIN, UserConstants.STATUS_ENABLED),
                createUser("manager", "运营经理", UserConstants.ROLE_OPERATOR, UserConstants.STATUS_ENABLED),
                createUser("auditor", "审计专员", UserConstants.ROLE_READONLY, UserConstants.STATUS_DISABLED)
        ));
    }

    /**
     * Creates a UserEntity object for initial demo data.
     */
    private UserEntity createUser(String username, String nickname, String role, String status) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(role);
        user.setStatus(status);
        return user;
    }

    /**
     * 把旧数据库里的明文密码升级成 BCrypt 密文。
     *
     * 之前学习阶段保存的是 "123456" 明文；升级后如果不迁移，登录时 matches 会校验失败。
     */
    private void upgradePlainTextPasswords() {
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            if ("123456".equals(user.getPassword())) {
                user.setPassword(passwordEncoder.encode("123456"));
                userRepository.save(user);
            }
        }
    }
}
