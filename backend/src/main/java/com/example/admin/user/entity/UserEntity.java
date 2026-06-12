package com.example.admin.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

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

    /**
     * Time when this row was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Time when this row was last updated.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Runs automatically before inserting a new row.
     *
     * We set both createdAt and updatedAt when the user is first saved.
     */
    @PrePersist
    public void beforeCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Runs automatically before updating an existing row.
     *
     * Only updatedAt changes here; createdAt should keep the original value.
     */
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
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
