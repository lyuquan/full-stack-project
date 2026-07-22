package com.example.admin.auth.constant;

import com.example.admin.user.constant.UserConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Authentication and authorization permission constants.
 *
 * Permission codes are stable strings used by the backend and frontend.
 * Roles describe "who the user is"; permissions describe "what the user can do".
 */
public final class AuthPermissions {

    /**
     * Permission for reading user data.
     */
    public static final String USER_READ = "user:read";

    /**
     * Permission for creating, updating, enabling, disabling and deleting users.
     */
    public static final String USER_WRITE = "user:write";

    /**
     * Permission for showing and entering the role management menu.
     */
    public static final String ROLE_MANAGE = "role:manage";

    private AuthPermissions() {
    }

    /**
     * List every permission code supported by this learning project.
     *
     * Other modules use this method to validate frontend-submitted permission
     * codes before saving them.
     */
    public static List<String> listAllCodes() {
        return Arrays.asList(USER_READ, USER_WRITE, ROLE_MANAGE);
    }

    /**
     * Build permission codes by role.
     *
     * Super admin owns read and write permissions.
     * Other enabled roles can read user data, but cannot change it.
     */
    public static List<String> listByRole(String role) {
        if (UserConstants.ROLE_SUPER_ADMIN.equals(role)) {
            return Arrays.asList(USER_READ, USER_WRITE, ROLE_MANAGE);
        }

        return Collections.singletonList(USER_READ);
    }

    /**
     * Check whether a permission list contains user write permission.
     */
    public static boolean canManageUsers(List<String> permissions) {
        return permissions != null && permissions.contains(USER_WRITE);
    }

    /**
     * Check whether a permission list contains role management permission.
     */
    public static boolean canManageRoles(List<String> permissions) {
        return permissions != null && permissions.contains(ROLE_MANAGE);
    }
}
