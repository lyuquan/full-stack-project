package com.example.admin.auth.controller;

import com.example.admin.auth.constant.AuthPermissions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 认证接口测试。
 *
 * 这些测试模拟前端登录、查询当前用户、退出登录等请求。
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-controller-test;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"
})
class AuthControllerTest {

    /**
     * MockMvc 用来在测试里模拟 HTTP 请求。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper 用来解析登录接口返回的 JSON。
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 启用状态的演示用户，应该可以用默认密码登录成功。
     */
    @Test
    void loginShouldReturnUserInfoWhenPasswordIsCorrect() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.username", is("admin")))
                .andExpect(jsonPath("$.data.nickname", is("系统管理员")))
                .andExpect(jsonPath("$.data.role", is("超级管理员")))
                .andExpect(jsonPath("$.data.permissions", hasSize(3)))
                .andExpect(jsonPath("$.data.permissions[0]", is(AuthPermissions.USER_READ)))
                .andExpect(jsonPath("$.data.permissions[1]", is(AuthPermissions.USER_WRITE)))
                .andExpect(jsonPath("$.data.permissions[2]", is(AuthPermissions.ROLE_MANAGE)))
                .andExpect(jsonPath("$.data.canManageUsers", is(true)))
                .andExpect(jsonPath("$.data.token", startsWith("study-token-")));
    }

    /**
     * 运营管理员可以登录，但只拥有查询用户的权限，不能修改用户数据。
     */
    @Test
    void loginShouldReturnReadOnlyPermissionsWhenUserIsNotSuperAdmin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"manager\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.username", is("manager")))
                .andExpect(jsonPath("$.data.permissions", hasSize(1)))
                .andExpect(jsonPath("$.data.permissions[0]", is(AuthPermissions.USER_READ)))
                .andExpect(jsonPath("$.data.canManageUsers", is(false)))
                .andExpect(jsonPath("$.data.token", startsWith("study-token-")));
    }

    /**
     * 密码错误时，应该返回业务错误。
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
     * 禁用用户不允许登录。
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

    /**
     * 带有效 token 查询 /api/auth/me 时，应该返回当前登录用户。
     */
    @Test
    void meShouldReturnCurrentLoginUser() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", loginAndGetAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.username", is("admin")))
                .andExpect(jsonPath("$.data.nickname", is("系统管理员")))
                .andExpect(jsonPath("$.data.role", is("超级管理员")))
                .andExpect(jsonPath("$.data.permissions", hasSize(3)))
                .andExpect(jsonPath("$.data.permissions[0]", is(AuthPermissions.USER_READ)))
                .andExpect(jsonPath("$.data.permissions[1]", is(AuthPermissions.USER_WRITE)))
                .andExpect(jsonPath("$.data.permissions[2]", is(AuthPermissions.ROLE_MANAGE)))
                .andExpect(jsonPath("$.data.canManageUsers", is(true)));
    }

    /**
     * 超级管理员拥有用户管理和角色管理权限，所以菜单接口应该返回三个菜单。
     */
    @Test
    void menusShouldReturnAllMenusForSuperAdmin() throws Exception {
        mockMvc.perform(get("/api/auth/menus")
                        .header("Authorization", loginAndGetAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].key", is("home")))
                .andExpect(jsonPath("$.data[0].path", is("/")))
                .andExpect(jsonPath("$.data[0].active", is(true)))
                .andExpect(jsonPath("$.data[1].key", is("users")))
                .andExpect(jsonPath("$.data[1].path", is("/users")))
                .andExpect(jsonPath("$.data[2].key", is("roles")))
                .andExpect(jsonPath("$.data[2].path", is("/roles")));
    }

    /**
     * 运营管理员没有 role:manage 权限，所以不应该看到角色管理菜单。
     */
    @Test
    void menusShouldHideRoleMenuForOperator() throws Exception {
        mockMvc.perform(get("/api/auth/menus")
                        .header("Authorization", loginAndGetAuthorizationHeader("manager")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].key", is("home")))
                .andExpect(jsonPath("$.data[0].path", is("/")))
                .andExpect(jsonPath("$.data[1].key", is("users")))
                .andExpect(jsonPath("$.data[1].path", is("/users")));
    }

    /**
     * 退出登录后，同一个 token 应该失效。
     */
    @Test
    void logoutShouldRemoveCurrentToken() throws Exception {
        String authorization = loginAndGetAuthorizationHeader();

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data").doesNotExist());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", authorization))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", is("请先登录")));
    }

    /**
     * 登录并返回 Authorization 请求头值。
     */
    private String loginAndGetAuthorizationHeader() throws Exception {
        return loginAndGetAuthorizationHeader("admin");
    }

    /**
     * 用指定账号登录，并返回 Authorization 请求头。
     */
    private String loginAndGetAuthorizationHeader(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = root.path("data").path("token").asText();

        return "Bearer " + token;
    }
}
