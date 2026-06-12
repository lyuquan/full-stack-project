package com.example.admin.user.vo;

/**
 * User statistics returned to the frontend dashboard.
 *
 * This VO does not represent one user. It represents summary numbers of the user table.
 */
public class UserStatsVO {

    /**
     * Total number of users in the database.
     */
    private long totalCount;

    /**
     * Number of users whose status is enabled.
     */
    private long enabledCount;

    /**
     * Number of users whose status is disabled.
     */
    private long disabledCount;

    /**
     * Number of users whose role is super admin.
     */
    private long superAdminCount;

    /**
     * Number of users whose role is operation admin.
     */
    private long operatorCount;

    /**
     * Number of users whose role is read-only user.
     */
    private long readonlyCount;

    public UserStatsVO() {
    }

    public UserStatsVO(
            long totalCount,
            long enabledCount,
            long disabledCount,
            long superAdminCount,
            long operatorCount,
            long readonlyCount
    ) {
        this.totalCount = totalCount;
        this.enabledCount = enabledCount;
        this.disabledCount = disabledCount;
        this.superAdminCount = superAdminCount;
        this.operatorCount = operatorCount;
        this.readonlyCount = readonlyCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getEnabledCount() {
        return enabledCount;
    }

    public void setEnabledCount(long enabledCount) {
        this.enabledCount = enabledCount;
    }

    public long getDisabledCount() {
        return disabledCount;
    }

    public void setDisabledCount(long disabledCount) {
        this.disabledCount = disabledCount;
    }

    public long getSuperAdminCount() {
        return superAdminCount;
    }

    public void setSuperAdminCount(long superAdminCount) {
        this.superAdminCount = superAdminCount;
    }

    public long getOperatorCount() {
        return operatorCount;
    }

    public void setOperatorCount(long operatorCount) {
        this.operatorCount = operatorCount;
    }

    public long getReadonlyCount() {
        return readonlyCount;
    }

    public void setReadonlyCount(long readonlyCount) {
        this.readonlyCount = readonlyCount;
    }
}
