package com.example.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后端项目启动类。
 *
 * @SpringBootApplication 是 Spring Boot 的核心注解：
 * 1. 开启自动配置，帮我们自动装配 Web、JSON、Tomcat 等能力。
 * 2. 扫描当前包 com.example.admin 以及它下面的 Controller、Service 等组件。
 * 3. 标记这是一个可以直接启动的 Spring Boot 应用。
 */
@SpringBootApplication
public class AdminApplication {

    /**
     * Java 程序入口。
     *
     * 运行这个 main 方法后，Spring Boot 会启动内置 Tomcat，
     * 后端接口就可以通过 http://localhost:8080 访问。
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
