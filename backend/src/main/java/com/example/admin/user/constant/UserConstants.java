package com.example.admin.user.constant;

/**
 * Constants used by the user module.
 *
 * Centralizing fixed role and status values avoids repeating the same strings
 * in DTO validation, statistics queries and option APIs.
 */
public final class UserConstants {

    /**
     * Super admin role value.
     */
    public static final String ROLE_SUPER_ADMIN = "超级管理员";

    /**
     * Operation admin role value.
     */
    public static final String ROLE_OPERATOR = "运营管理员";

    /**
     * Read-only role value.
     */
    public static final String ROLE_READONLY = "只读用户";

    /**
     * Regular expression used by DTO validation to allow only supported roles.
     */
    public static final String ROLE_PATTERN = ROLE_SUPER_ADMIN + "|" + ROLE_OPERATOR + "|" + ROLE_READONLY;

    /**
     * Enabled status value stored in the database.
     */
    public static final String STATUS_ENABLED = "enabled";

    /**
     * Disabled status value stored in the database.
     */
    public static final String STATUS_DISABLED = "disabled";

    /**
     * Regular expression used by DTO validation to allow only supported statuses.
     */
    public static final String STATUS_PATTERN = STATUS_ENABLED + "|" + STATUS_DISABLED;

    /**
     * Display label for enabled status.
     */
    public static final String STATUS_ENABLED_LABEL = "启用";

    /**
     * Display label for disabled status.
     */
    public static final String STATUS_DISABLED_LABEL = "禁用";

    private UserConstants() {
    }
}
