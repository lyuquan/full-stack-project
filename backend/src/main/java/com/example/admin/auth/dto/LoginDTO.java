package com.example.admin.auth.dto;

import javax.validation.constraints.NotBlank;

/**
 * Login request parameters.
 *
 * DTO is used to receive JSON fields sent by the frontend login form.
 */
public class LoginDTO {

    /**
     * Login username entered on the frontend.
     */
    @NotBlank(message = "账号不能为空")
    private String username;

    /**
     * Login password entered on the frontend.
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
