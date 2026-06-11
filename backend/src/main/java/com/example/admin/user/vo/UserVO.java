package com.example.admin.user.vo;

/**
 * 用户列表返回给前端的视图对象。
 *
 * VO 是 View Object 的缩写，意思是“给页面展示用的对象”。
 * 后端真实项目里，数据库字段不一定全部返回给前端，所以会单独定义 VO 控制输出内容。
 */
public class UserVO {

    /**
     * 用户 ID。
     * 后续接数据库后，它通常对应用户表的主键。
     */
    private Long id;

    /**
     * 登录账号。
     * 后台管理系统通常用它作为登录名或列表展示字段。
     */
    private String username;

    /**
     * 用户昵称。
     * 它比账号更适合直接展示给运营或管理员看。
     */
    private String nickname;

    /**
     * 用户角色。
     * 这里先用字符串演示，后续可以扩展成角色表和角色 ID。
     */
    private String role;

    /**
     * 用户状态。
     * 这里用 enabled / disabled 演示启用和禁用状态。
     */
    private String status;

    public UserVO() {
    }

    public UserVO(Long id, String username, String nickname, String role, String status) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
