package com.example.admin.role.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request body for creating a role.
 *
 * DTO means Data Transfer Object. The frontend sends JSON, and Spring Boot
 * converts that JSON into this Java object before it reaches Service.
 */
public class CreateRoleDTO {

    /**
     * Stable role code used by APIs and program logic.
     *
     * The regexp allows lowercase letters, numbers and underscores only. This
     * keeps URL paths such as /api/roles/report_admin simple and predictable.
     */
    @NotBlank(message = "Role code cannot be blank")
    @Size(max = 50, message = "Role code cannot be longer than 50 characters")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "Role code must start with lowercase letter and only contain lowercase letters, numbers or underscores")
    private String code;

    /**
     * Role name displayed on the frontend page.
     */
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 50, message = "Role name cannot be longer than 50 characters")
    private String name;

    /**
     * Short role description displayed on the frontend page.
     */
    @NotBlank(message = "Role description cannot be blank")
    @Size(max = 200, message = "Role description cannot be longer than 200 characters")
    private String description;

    /**
     * Learning field that shows how many permissions this role owns.
     */
    @NotNull(message = "Permission count cannot be null")
    @Min(value = 0, message = "Permission count cannot be less than 0")
    @Max(value = 999, message = "Permission count cannot be greater than 999")
    private Integer permissionCount;

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
