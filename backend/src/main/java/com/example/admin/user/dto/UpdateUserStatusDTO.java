package com.example.admin.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Request body for changing only a user's status.
 *
 * It is separated from UpdateUserDTO because enabling or disabling a user
 * should only submit the status field, not the full user form.
 */
public class UpdateUserStatusDTO {

    /**
     * User status.
     *
     * Only enabled and disabled are allowed, so invalid frontend values are
     * blocked by backend validation.
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "enabled|disabled", message = "状态只能是 enabled 或 disabled")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
