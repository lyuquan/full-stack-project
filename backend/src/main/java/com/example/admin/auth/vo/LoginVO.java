package com.example.admin.auth.vo;

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

    public LoginVO() {
    }

    public LoginVO(Long id, String username, String nickname, String role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
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
}
