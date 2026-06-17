package com.example.admin.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Interface tests for login APIs.
 *
 * These tests simulate frontend login requests and verify the backend response.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-controller-test;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"
})
class AuthControllerTest {

    /**
     * MockMvc is used to send fake HTTP requests to the controller in tests.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Enabled demo user should login successfully with the default password.
     */
    @Test
    void loginShouldReturnUserInfoWhenPasswordIsCorrect() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.username", is("admin")))
                .andExpect(jsonPath("$.data.role", is("超级管理员")))
                .andExpect(jsonPath("$.data.token", startsWith("study-token-")));
    }

    /**
     * Wrong password should return a business error.
     */
    @Test
    void loginShouldFailWhenPasswordIsWrong() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("账号或密码错误")));
    }

    /**
     * Disabled users should not be allowed to login.
     */
    @Test
    void loginShouldFailWhenUserIsDisabled() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"auditor\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("账号已被禁用")));
    }
}
