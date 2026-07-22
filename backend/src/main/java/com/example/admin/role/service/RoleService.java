package com.example.admin.role.service;

import com.example.admin.role.entity.RoleEntity;
import com.example.admin.role.repository.RoleRepository;
import com.example.admin.role.vo.RoleVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Role business service.
 *
 * Service handles role business logic and converts database entities into VO
 * objects that are safe for the frontend page.
 */
@Service
public class RoleService {

    /**
     * Role repository used to query the sys_role table.
     */
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Query roles visible on the role management page.
     */
    public List<RoleVO> listRoles() {
        List<RoleEntity> roleEntities = roleRepository.findAllByOrderByIdAsc();
        List<RoleVO> roles = new ArrayList<RoleVO>();

        for (RoleEntity roleEntity : roleEntities) {
            roles.add(toVO(roleEntity));
        }

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
        Optional<RoleEntity> role = roleRepository.findByCode(code);

        if (!role.isPresent()) {
            return null;
        }

        return toVO(role.get());
    }

    /**
     * Convert database entity to frontend view object.
     *
     * Keeping this conversion in Service means Controller does not need to know
     * the database table structure.
     */
    private RoleVO toVO(RoleEntity roleEntity) {
        return new RoleVO(
                roleEntity.getCode(),
                roleEntity.getName(),
                roleEntity.getDescription(),
                roleEntity.getPermissionCount()
        );
    }
}
