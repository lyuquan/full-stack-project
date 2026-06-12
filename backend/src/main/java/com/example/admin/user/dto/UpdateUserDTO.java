package com.example.admin.user.dto;

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
     * Role name.
     *
     * @Pattern restricts role to the three roles supported by the current page.
     * This prevents API clients from editing a user into an unsupported role.
     */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "超级管理员|运营管理员|只读用户", message = "角色只能是 超级管理员、运营管理员 或 只读用户")
    private String role;

    /**
     * User status.
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "enabled|disabled", message = "状态只能是 enabled 或 disabled")
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
