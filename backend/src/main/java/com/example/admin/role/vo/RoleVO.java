package com.example.admin.role.vo;

/**
 * Role data returned to the frontend role management page.
 *
 * VO means View Object: it is shaped for page display, not for direct database
 * storage. This step uses fixed role data first, so there is no RoleEntity yet.
 */
public class RoleVO {

    /**
     * Stable role code used by the program.
     */
    private String code;

    /**
     * Role name displayed on the page.
     */
    private String name;

    /**
     * Short description that explains what this role can do.
     */
    private String description;

    /**
     * Number of permissions owned by this role in the learning version.
     */
    private Integer permissionCount;

    public RoleVO() {
    }

    public RoleVO(String code, String name, String description, Integer permissionCount) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.permissionCount = permissionCount;
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

    public Integer getPermissionCount() {
        return permissionCount;
    }

    public void setPermissionCount(Integer permissionCount) {
        this.permissionCount = permissionCount;
    }
}
