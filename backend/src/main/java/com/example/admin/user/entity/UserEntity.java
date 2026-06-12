package com.example.admin.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User database entity.
 *
 * Entity maps Java objects to database table rows.
 * This class maps to the sys_user table.
 */
@Entity
@Table(name = "sys_user")
public class UserEntity {

    /**
     * Primary key.
     *
     * IDENTITY means the database generates the ID when inserting a new row.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Login username.
     */
    @Column(nullable = false, length = 30)
    private String username;

    /**
     * Display name shown in the admin page.
     */
    @Column(nullable = false, length = 30)
    private String nickname;

    /**
     * User role name.
     */
    @Column(nullable = false, length = 30)
    private String role;

    /**
     * User status: enabled or disabled.
     */
    @Column(nullable = false, length = 20)
    private String status;

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
