package com.example.admin.user.dto;

/**
 * 新增用户请求参数对象。
 *
 * DTO 是 Data Transfer Object 的缩写，意思是“数据传输对象”。
 * 前端提交 JSON 到后端时，后端会用 DTO 接收这些请求参数。
 */
public class CreateUserDTO {

    /**
     * 登录账号。
     * 前端 JSON 里的 username 会自动映射到这个字段。
     */
    private String username;

    /**
     * 用户昵称。
     * 前端 JSON 里的 nickname 会自动映射到这个字段。
     */
    private String nickname;

    /**
     * 用户角色。
     * 前端 JSON 里的 role 会自动映射到这个字段。
     */
    private String role;

    /**
     * 用户状态。
     * 前端 JSON 里的 status 会自动映射到这个字段。
     */
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
