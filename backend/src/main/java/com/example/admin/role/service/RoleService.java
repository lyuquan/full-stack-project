package com.example.admin.role.service;

import com.example.admin.role.vo.RoleVO;
import com.example.admin.user.constant.UserConstants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Role business service.
 *
 * The project does not have a role table yet. This service returns fixed role
 * data first, then later we can replace the fixed list with database queries.
 */
@Service
public class RoleService {

    /**
     * Query roles visible on the role management page.
     */
    public List<RoleVO> listRoles() {
        List<RoleVO> roles = new ArrayList<RoleVO>();

        roles.add(new RoleVO(
                "super_admin",
                UserConstants.ROLE_SUPER_ADMIN,
                "Owns user query, user write and role management permissions.",
                3
        ));
        roles.add(new RoleVO(
                "operator",
                UserConstants.ROLE_OPERATOR,
                "Can query user data but cannot change users or roles.",
                1
        ));
        roles.add(new RoleVO(
                "readonly",
                UserConstants.ROLE_READONLY,
                "Disabled demo account used to learn login status checks.",
                0
        ));

        return roles;
    }

    /**
     * Query one role by role code.
     *
     * code comes from the URL path, for example GET /api/roles/operator.
     * Returning null means the role does not exist, and Controller will turn it
     * into a unified 404 response for the frontend.
     */
    public RoleVO getRoleByCode(String code) {
        List<RoleVO> roles = listRoles();

        for (RoleVO role : roles) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }

        return null;
    }
}
