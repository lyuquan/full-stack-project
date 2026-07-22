package com.example.admin.user.vo;

import java.time.LocalDateTime;

/**
 * User view object returned to the frontend.
 *
 * VO means View Object. It controls what fields are exposed to the page.
 * Database fields are not always returned directly in real projects.
 */
public class UserVO {

    /**
     * User ID.
     */
    private Long id;

    /**
     * Login username.
     */
    private String username;

    /**
     * Display nickname.
     */
    private String nickname;

    /**
     * Stable role code.
     */
    private String roleCode;

    /**
     * User role name.
     */
    private String role;

    /**
     * User status: enabled or disabled.
     */
    private String status;

    /**
     * Time when this user was created.
     */
    private LocalDateTime createdAt;

    /**
     * Time when this user was last updated.
     */
    private LocalDateTime updatedAt;

    public UserVO() {
    }

    public UserVO(
            Long id,
            String username,
            String nickname,
            String roleCode,
            String role,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.roleCode = roleCode;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
