package com.example.admin.role.controller;

import com.example.admin.user.constant.UserConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
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
 * Role API tests.
 *
 * These tests make sure the role list endpoint requires login and returns the
 * role data structure expected by the frontend.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:role-controller-test;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"
})
class RoleControllerTest {

    /**
     * MockMvc simulates HTTP requests without opening a browser.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper reads the login response JSON so the test can extract token.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Logged-in users should be able to query role list data.
     */
    @Test
    void listRolesShouldReturnRoleListWhenLoggedIn() throws Exception {
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].code", is("super_admin")))
                .andExpect(jsonPath("$.data[0].name", is(UserConstants.ROLE_SUPER_ADMIN)))
                .andExpect(jsonPath("$.data[0].permissionCount", is(3)))
                .andExpect(jsonPath("$.data[1].code", is("operator")))
                .andExpect(jsonPath("$.data[2].code", is("readonly")));
    }

    /**
     * Role list is protected by the login interceptor.
     */
    @Test
    void listRolesShouldRequireLoginToken() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", is("请先登录")));
    }

    /**
     * Logged-in users can query one role detail by role code.
     */
    @Test
    void getRoleDetailShouldReturnRoleWhenLoggedIn() throws Exception {
        mockMvc.perform(get("/api/roles/operator")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.code", is("operator")))
                .andExpect(jsonPath("$.data.name", is(UserConstants.ROLE_OPERATOR)))
                .andExpect(jsonPath("$.data.permissionCount", is(1)));
    }

    /**
     * Unknown role code should return a unified business 404 response.
     */
    @Test
    void getRoleDetailShouldReturn404WhenRoleNotFound() throws Exception {
        mockMvc.perform(get("/api/roles/unknown")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Role does not exist")));
    }

    /**
     * Logged-in users can create a new role.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createRoleShouldSaveRoleWhenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"report_admin\",\"name\":\"Report Admin\",\"description\":\"Can view report pages.\",\"permissionCount\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.code", is("report_admin")))
                .andExpect(jsonPath("$.data.name", is("Report Admin")))
                .andExpect(jsonPath("$.data.permissionCount", is(2)));
    }

    /**
     * Role code is unique, so creating the same role twice should fail.
     */
    @Test
    void createRoleShouldReturn400WhenCodeAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"operator\",\"name\":\"Operator Copy\",\"description\":\"Duplicate code.\",\"permissionCount\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("Role code already exists")));
    }

    /**
     * DTO validation should reject invalid role code format before Service saves data.
     */
    @Test
    void createRoleShouldReturn400WhenCodeFormatIsInvalid() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"Report Admin\",\"name\":\"Report Admin\",\"description\":\"Invalid code format.\",\"permissionCount\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("Role code must start with lowercase letter and only contain lowercase letters, numbers or underscores")));
    }

    private String getAuthorizationHeader() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = root.path("data").path("token").asText();

        return "Bearer " + token;
    }
}
