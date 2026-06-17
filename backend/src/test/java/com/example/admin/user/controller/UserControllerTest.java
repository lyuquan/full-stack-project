package com.example.admin.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.admin.user.constant.UserConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户接口测试。
 *
 * 用户接口已经被登录拦截器保护，所以测试访问用户接口前，也要先登录拿到真实 token。
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:user-controller-test;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"
})
class UserControllerTest {

    /**
     * MockMvc 是 Spring 的接口测试工具，可以不启动浏览器就模拟 HTTP 请求。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper 用来把登录接口返回的 JSON 字符串解析成对象。
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 用户统计接口应该返回用户汇总数字。
     */
    @Test
    void getUserStatsShouldReturnCounts() throws Exception {
        mockMvc.perform(get("/api/users/stats")
                        .header("Authorization", getAuthorizationHeader()))
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
     * 角色选项接口应该返回当前用户模块支持的全部角色。
     */
    @Test
    void listRoleOptionsShouldReturnSupportedRoles() throws Exception {
        mockMvc.perform(get("/api/users/roles")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].value", is(UserConstants.ROLE_SUPER_ADMIN)))
                .andExpect(jsonPath("$.data[1].value", is(UserConstants.ROLE_OPERATOR)))
                .andExpect(jsonPath("$.data[2].value", is(UserConstants.ROLE_READONLY)));
    }

    /**
     * 状态选项接口应该返回启用和禁用两个状态。
     */
    @Test
    void listStatusOptionsShouldReturnSupportedStatuses() throws Exception {
        mockMvc.perform(get("/api/users/statuses")
                        .header("Authorization", getAuthorizationHeader()))
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

    /**
     * 随便伪造一个 study-token 也不能访问用户接口。
     *
     * 这一点和上一步不同：现在后端会检查 token 是否真的登记在 AuthTokenService 里。
     */
    @Test
    void userApiShouldRejectFakeToken() throws Exception {
        mockMvc.perform(get("/api/users/stats")
                        .header("Authorization", "Bearer study-token-fake"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", is("请先登录")));
    }

    /**
     * 先调用登录接口，再从返回 JSON 里取出 token。
     */
    private String getAuthorizationHeader() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        String token = root.path("data").path("token").asText();

        return "Bearer " + token;
    }
}
