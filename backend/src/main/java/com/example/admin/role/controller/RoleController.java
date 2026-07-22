package com.example.admin.role.controller;

import com.example.admin.common.ApiResponse;
import com.example.admin.role.dto.CreateRoleDTO;
import com.example.admin.role.dto.UpdateRolePermissionsDTO;
import com.example.admin.role.dto.UpdateRoleDTO;
import com.example.admin.role.service.RoleService;
import com.example.admin.role.vo.RoleVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Role management API entrance.
 *
 * Controller receives frontend HTTP requests and delegates real business work
 * to RoleService. This keeps request handling and business rules separated.
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    /**
     * Role business service injected by Spring.
     */
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Query role list.
     *
     * Example: GET /api/roles
     */
    @GetMapping
    public ApiResponse<List<RoleVO>> listRoles() {
        return ApiResponse.success(roleService.listRoles());
    }

    /**
     * Query one role detail by role code.
     *
     * Example: GET /api/roles/operator
     */
    @GetMapping("/{code}")
    public ApiResponse<RoleVO> getRoleDetail(@PathVariable String code) {
        RoleVO role = roleService.getRoleByCode(code);

        if (role == null) {
            return ApiResponse.error(404, "Role does not exist");
        }

        return ApiResponse.success(role);
    }

    /**
     * Create one role.
     *
     * Example: POST /api/roles
     */
    @PostMapping
    public ApiResponse<RoleVO> createRole(@Valid @RequestBody CreateRoleDTO createRoleDTO) {
        RoleVO role = roleService.createRole(createRoleDTO);

        return ApiResponse.success(role);
    }

    /**
     * Update one role by code.
     *
     * Example: PUT /api/roles/operator
     */
    @PutMapping("/{code}")
    public ApiResponse<RoleVO> updateRole(
            @PathVariable String code,
            @Valid @RequestBody UpdateRoleDTO updateRoleDTO
    ) {
        RoleVO role = roleService.updateRole(code, updateRoleDTO);

        if (role == null) {
            return ApiResponse.error(404, "Role does not exist");
        }

        return ApiResponse.success(role);
    }

    /**
     * Delete one role by code.
     *
     * Example: DELETE /api/roles/operator
     *
     * Delete success does not need to return role data, so data is null.
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> deleteRole(@PathVariable String code) {
        boolean deleted = roleService.deleteRole(code);

        if (!deleted) {
            return ApiResponse.error(404, "Role does not exist");
        }

        return ApiResponse.success(null);
    }

    /**
     * Replace permissions owned by one role.
     *
     * Example: PATCH /api/roles/operator/permissions
     */
    @PatchMapping("/{code}/permissions")
    public ApiResponse<RoleVO> updateRolePermissions(
            @PathVariable String code,
            @Valid @RequestBody UpdateRolePermissionsDTO updateRolePermissionsDTO
    ) {
        RoleVO role = roleService.updateRolePermissions(code, updateRolePermissionsDTO);

        if (role == null) {
            return ApiResponse.error(404, "Role does not exist");
        }

        return ApiResponse.success(role);
    }
}
