package com.example.admin.user.dto;

import com.example.admin.user.constant.UserConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request body for updating a user.
 *
 * It is separated from CreateUserDTO because create and update may have
 * different rules in real projects.
 */
public class UpdateUserDTO {

    /**
     * Login username.
     */
    @NotBlank(message = "账号不能为空")
    @Size(max = 30, message = "账号不能超过30个字符")
    private String username;

    /**
     * Display name.
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称不能超过30个字符")
    private String nickname;

    /**
     * Stable role code selected by the frontend.
     */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = UserConstants.ROLE_CODE_PATTERN, message = "角色编码只能是 super_admin、operator 或 readonly")
    private String roleCode;

    /**
     * User status.
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = UserConstants.STATUS_PATTERN, message = "状态只能是 enabled 或 disabled")
    private String status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
