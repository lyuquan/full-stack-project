package com.example.admin.role.service;

import com.example.admin.common.BusinessException;
import com.example.admin.role.dto.CreateRoleDTO;
import com.example.admin.role.dto.UpdateRoleDTO;
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
     * Create one role from frontend form data.
     *
     * Service does business validation here because duplicate role code depends
     * on database data, not just request format.
     */
    public RoleVO createRole(CreateRoleDTO createRoleDTO) {
        if (roleRepository.existsByCode(createRoleDTO.getCode())) {
            throw new BusinessException(400, "Role code already exists");
        }

        RoleEntity role = new RoleEntity();
        role.setCode(createRoleDTO.getCode());
        role.setName(createRoleDTO.getName());
        role.setDescription(createRoleDTO.getDescription());
        role.setPermissionCount(createRoleDTO.getPermissionCount());

        return toVO(roleRepository.save(role));
    }

    /**
     * Update one existing role by code.
     *
     * Returning null means the URL code does not match any database row.
     */
    public RoleVO updateRole(String code, UpdateRoleDTO updateRoleDTO) {
        Optional<RoleEntity> optionalRole = roleRepository.findByCode(code);

        if (!optionalRole.isPresent()) {
            return null;
        }

        RoleEntity role = optionalRole.get();
        role.setName(updateRoleDTO.getName());
        role.setDescription(updateRoleDTO.getDescription());
        role.setPermissionCount(updateRoleDTO.getPermissionCount());

        return toVO(roleRepository.save(role));
    }

    /**
     * Delete one existing role by code.
     *
     * The method returns boolean because Controller only needs to know whether
     * a row was deleted, not the deleted role content.
     */
    public boolean deleteRole(String code) {
        Optional<RoleEntity> optionalRole = roleRepository.findByCode(code);

        if (!optionalRole.isPresent()) {
            return false;
        }

        roleRepository.delete(optionalRole.get());

        return true;
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
