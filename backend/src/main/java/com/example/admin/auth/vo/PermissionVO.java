package com.example.admin.auth.vo;

/**
 * Permission data returned to the frontend.
 *
 * A permission is a stable action code, such as user:read. The frontend can
 * display the name and description, while backend logic still uses the code.
 */
public class PermissionVO {

    /**
     * Stable permission code used by backend checks and frontend buttons.
     */
    private String code;

    /**
     * Human-readable permission name shown on the page.
     */
    private String name;

    /**
     * Short explanation of what this permission allows.
     */
    private String description;

    public PermissionVO() {
    }

    public PermissionVO(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
