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

    private AuthPermissions() {
    }

    /**
     * Build permission codes by role.
     *
     * Super admin owns read and write permissions.
     * Other enabled roles can read user data, but cannot change it.
     */
    public static List<String> listByRole(String role) {
        if (UserConstants.ROLE_SUPER_ADMIN.equals(role)) {
            return Arrays.asList(USER_READ, USER_WRITE);
        }

        return Collections.singletonList(USER_READ);
    }

    /**
     * Check whether a permission list contains user write permission.
     */
    public static boolean canManageUsers(List<String> permissions) {
        return permissions != null && permissions.contains(USER_WRITE);
    }
}
