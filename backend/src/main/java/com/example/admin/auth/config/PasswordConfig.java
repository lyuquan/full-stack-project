package com.example.admin.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码相关配置。
 *
 * @Configuration 表示这是一个 Spring 配置类。
 * @Bean 表示把 BCryptPasswordEncoder 对象交给 Spring 管理，其他类可以通过构造方法注入使用。
 */
@Configuration
public class PasswordConfig {

    /**
     * BCryptPasswordEncoder 用来生成 BCrypt 密码密文，也用来校验明文密码是否匹配密文。
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
