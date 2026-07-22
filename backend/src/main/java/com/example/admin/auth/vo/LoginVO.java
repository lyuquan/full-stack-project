package com.example.admin.auth.vo;

import java.util.List;

/**
 * Login response data returned to the frontend.
 *
 * VO controls what the frontend can see after login.
 */
public class LoginVO {

    /**
     * Logged-in user's ID.
     */
    private Long id;

    /**
     * Logged-in username.
     */
    private String username;

    /**
     * Display name shown in the admin page.
     */
    private String nickname;

    /**
     * User role used by the frontend to show current identity.
     */
    private String role;

    /**
     * Permission codes owned by the current login user.
     *
     * The frontend can check codes such as user:read and user:write to decide
     * which buttons should be enabled.
     */
    private List<String> permissions;

    /**
     * Whether the logged-in user can manage user data.
     *
     * The frontend can use this field to enable or disable user action buttons
     * without guessing permission from role text.
     */
    private Boolean canManageUsers;

    /**
     * 登录成功后返回给前端的学习版 token。
     *
     * 前端会保存这个值，用来刷新页面后恢复登录状态。
     */
    private String token;

    public LoginVO() {
    }

    public LoginVO(Long id, String username, String nickname, String role, List<String> permissions, Boolean canManageUsers, String token) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.permissions = permissions;
        this.canManageUsers = canManageUsers;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Boolean getCanManageUsers() {
        return canManageUsers;
    }

    public void setCanManageUsers(Boolean canManageUsers) {
        this.canManageUsers = canManageUsers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
