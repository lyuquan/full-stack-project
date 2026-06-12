package com.example.admin.user.config;

import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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

    public UserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.saveAll(Arrays.asList(
                createUser("admin", "系统管理员", "超级管理员", "enabled"),
                createUser("manager", "运营经理", "运营管理员", "enabled"),
                createUser("auditor", "审计专员", "只读用户", "disabled")
        ));
    }

    /**
     * Creates a UserEntity object for initial demo data.
     */
    private UserEntity createUser(String username, String nickname, String role, String status) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
