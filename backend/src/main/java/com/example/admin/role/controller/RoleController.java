package com.example.admin.role.controller;

import com.example.admin.common.ApiResponse;
import com.example.admin.role.service.RoleService;
import com.example.admin.role.vo.RoleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
