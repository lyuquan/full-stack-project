package com.example.admin.role.controller;

import com.example.admin.user.constant.UserConstants;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
     * Users with role:manage should be able to query role list data.
     */
    @Test
    void listRolesShouldReturnRoleListWhenLoggedIn() throws Exception {
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data[0].code", is("super_admin")))
                .andExpect(jsonPath("$.data[0].name", is(UserConstants.ROLE_SUPER_ADMIN)))
                .andExpect(jsonPath("$.data[0].permissionCount", is(3)))
                .andExpect(jsonPath("$.data[1].code", is("operator")))
                .andExpect(jsonPath("$.data[2].code", is("readonly")));
    }

    /**
     * Logged-in users without role:manage should be rejected by the role permission interceptor.
     */
    @Test
    void listRolesShouldReturn403WhenUserHasNoRoleManagePermission() throws Exception {
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", getAuthorizationHeader("manager")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(403)))
                .andExpect(jsonPath("$.message", is("没有角色管理权限")));
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
                .andExpect(jsonPath("$.data.permissionCount", is(1)))
                .andExpect(jsonPath("$.data.permissionCodes[0]", is("user:read")));
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

    /**
     * Logged-in users can edit role display fields by role code.
     */
    @Test
    void updateRoleShouldUpdateRoleWhenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"edit_target\",\"name\":\"Edit Target\",\"description\":\"Role prepared for edit test.\",\"permissionCount\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)));

        mockMvc.perform(put("/api/roles/edit_target")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Operator Updated\",\"description\":\"Updated role description.\",\"permissionCount\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.code", is("edit_target")))
                .andExpect(jsonPath("$.data.name", is("Operator Updated")))
                .andExpect(jsonPath("$.data.description", is("Updated role description.")))
                .andExpect(jsonPath("$.data.permissionCount", is(5)));
    }

    /**
     * Updating an unknown role code should return a unified 404 response.
     */
    @Test
    void updateRoleShouldReturn404WhenRoleNotFound() throws Exception {
        mockMvc.perform(put("/api/roles/unknown")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Unknown\",\"description\":\"Unknown role.\",\"permissionCount\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Role does not exist")));
    }

    /**
     * Update DTO validation should reject invalid permission counts.
     */
    @Test
    void updateRoleShouldReturn400WhenPermissionCountIsInvalid() throws Exception {
        mockMvc.perform(put("/api/roles/operator")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Operator\",\"description\":\"Invalid count.\",\"permissionCount\":1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("Permission count cannot be greater than 999")));
    }

    /**
     * Logged-in users can delete one role by code.
     */
    @Test
    void deleteRoleShouldRemoveRoleWhenRoleExists() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"delete_target\",\"name\":\"Delete Target\",\"description\":\"Role prepared for delete test.\",\"permissionCount\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)));

        mockMvc.perform(delete("/api/roles/delete_target")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data").doesNotExist());

        mockMvc.perform(get("/api/roles/delete_target")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Role does not exist")));
    }

    /**
     * Deleting an unknown role code should return a unified 404 response.
     */
    @Test
    void deleteRoleShouldReturn404WhenRoleNotFound() throws Exception {
        mockMvc.perform(delete("/api/roles/unknown")
                        .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Role does not exist")));
    }

    /**
     * Logged-in users can replace permissions owned by one role.
     */
    @Test
    void updateRolePermissionsShouldReplacePermissionCodes() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"permission_target\",\"name\":\"Permission Target\",\"description\":\"Role prepared for permission test.\",\"permissionCount\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)));

        mockMvc.perform(patch("/api/roles/permission_target/permissions")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionCodes\":[\"user:read\",\"role:manage\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.code", is("permission_target")))
                .andExpect(jsonPath("$.data.permissionCount", is(2)))
                .andExpect(jsonPath("$.data.permissionCodes[0]", is("user:read")))
                .andExpect(jsonPath("$.data.permissionCodes[1]", is("role:manage")));
    }

    /**
     * Updating permissions for an unknown role should return 404.
     */
    @Test
    void updateRolePermissionsShouldReturn404WhenRoleNotFound() throws Exception {
        mockMvc.perform(patch("/api/roles/unknown/permissions")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionCodes\":[\"user:read\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Role does not exist")));
    }

    /**
     * Backend validation should reject permission codes outside the dictionary.
     */
    @Test
    void updateRolePermissionsShouldReturn400WhenPermissionCodeIsUnknown() throws Exception {
        mockMvc.perform(patch("/api/roles/operator/permissions")
                        .header("Authorization", getAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionCodes\":[\"unknown:permission\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("Permission code does not exist: unknown:permission")));
    }

    private String getAuthorizationHeader() throws Exception {
        return getAuthorizationHeader("admin");
    }

    /**
     * Login with the given username and build the Authorization header.
     */
    private String getAuthorizationHeader(String username) throws Exception {
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
