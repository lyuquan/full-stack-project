package com.example.admin.user.controller;

import com.example.admin.user.constant.UserConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Interface tests for user controller.
 *
 * MockMvc can call controller APIs inside tests without starting a real browser.
 * These tests help us quickly verify that important endpoints still return the expected JSON structure.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:user-controller-test;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"
})
class UserControllerTest {

    /**
     * 测试里使用的学习版登录请求头。
     *
     * 真实项目里 token 会先通过登录接口拿到，这里为了让用户接口测试专注于接口结果，
     * 直接准备一个符合拦截器规则的测试 token。
     */
    private static final String TEST_AUTHORIZATION = "Bearer study-token-test";

    /**
     * MockMvc is Spring's testing helper for simulating HTTP requests.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * The statistics API should return user summary numbers.
     */
    @Test
    void getUserStatsShouldReturnCounts() throws Exception {
        mockMvc.perform(get("/api/users/stats")
                        .header("Authorization", TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.totalCount", is(3)))
                .andExpect(jsonPath("$.data.enabledCount", is(2)))
                .andExpect(jsonPath("$.data.disabledCount", is(1)))
                .andExpect(jsonPath("$.data.superAdminCount", is(1)))
                .andExpect(jsonPath("$.data.operatorCount", is(1)))
                .andExpect(jsonPath("$.data.readonlyCount", is(1)));
    }

    /**
     * The role option API should return all roles used by the current user module.
     */
    @Test
    void listRoleOptionsShouldReturnSupportedRoles() throws Exception {
        mockMvc.perform(get("/api/users/roles")
                        .header("Authorization", TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].value", is(UserConstants.ROLE_SUPER_ADMIN)))
                .andExpect(jsonPath("$.data[1].value", is(UserConstants.ROLE_OPERATOR)))
                .andExpect(jsonPath("$.data[2].value", is(UserConstants.ROLE_READONLY)));
    }

    /**
     * The status option API should return enabled and disabled options.
     */
    @Test
    void listStatusOptionsShouldReturnSupportedStatuses() throws Exception {
        mockMvc.perform(get("/api/users/statuses")
                        .header("Authorization", TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].value", is(UserConstants.STATUS_ENABLED)))
                .andExpect(jsonPath("$.data[0].label", is(UserConstants.STATUS_ENABLED_LABEL)))
                .andExpect(jsonPath("$.data[1].value", is(UserConstants.STATUS_DISABLED)))
                .andExpect(jsonPath("$.data[1].label", is(UserConstants.STATUS_DISABLED_LABEL)));
    }

    /**
     * 没有 Authorization 请求头时，用户接口应该被登录拦截器拦住。
     */
    @Test
    void userApiShouldRequireLoginToken() throws Exception {
        mockMvc.perform(get("/api/users/stats"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", is("请先登录")));
    }
}
