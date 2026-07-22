package com.example.admin.role.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Request body for assigning permissions to one role.
 *
 * This DTO only updates permission codes. It does not update role name,
 * description or code, so the API intention stays clear.
 */
public class UpdateRolePermissionsDTO {

    /**
     * Permission codes selected by the frontend checkbox group.
     */
    @NotNull(message = "Permission codes cannot be null")
    @Size(max = 20, message = "Permission codes cannot contain more than 20 items")
    private List<String> permissionCodes;

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }
}
