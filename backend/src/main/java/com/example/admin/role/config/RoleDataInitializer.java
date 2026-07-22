package com.example.admin.role.config;

import com.example.admin.auth.constant.AuthPermissions;
import com.example.admin.role.entity.RoleEntity;
import com.example.admin.role.repository.RoleRepository;
import com.example.admin.user.constant.UserConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * Initializes demo roles when the role table is empty.
 *
 * CommandLineRunner runs after Spring Boot starts. We use it to prepare the
 * first role rows only once, then later role data can live in the H2 database.
 */
@Component
@Order(3)
public class RoleDataInitializer implements CommandLineRunner {

    /**
     * Repository used to check and insert demo roles.
     */
    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            backfillPermissionCodes("super_admin", "user:read,user:write,role:manage");
            backfillPermissionCodes("operator", AuthPermissions.USER_READ);
            backfillPermissionCodes("readonly", "");
            return;
        }

        roleRepository.saveAll(Arrays.asList(
                createRole(
                        "super_admin",
                        UserConstants.ROLE_SUPER_ADMIN,
                        "Owns user query, user write and role management permissions.",
                        "user:read,user:write,role:manage"
                ),
                createRole(
                        "operator",
                        UserConstants.ROLE_OPERATOR,
                        "Can query user data but cannot change users or roles.",
                        AuthPermissions.USER_READ
                ),
                createRole(
                        "readonly",
                        UserConstants.ROLE_READONLY,
                        "Disabled demo account used to learn login status checks.",
                        ""
                )
        ));
    }

    /**
     * Creates one RoleEntity object for initial demo data.
     */
    private RoleEntity createRole(String code, String name, String description, String permissionCodes) {
        RoleEntity role = new RoleEntity();
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setPermissionCodes(permissionCodes);
        role.setPermissionCount(permissionCodes.isEmpty() ? 0 : permissionCodes.split(",").length);
        return role;
    }

    /**
     * Fill permission codes for old H2 rows created before this step.
     *
     * It only updates rows whose permissionCodes is null, so user-edited
     * permissions will not be overwritten after later restarts.
     */
    private void backfillPermissionCodes(String code, String permissionCodes) {
        Optional<RoleEntity> optionalRole = roleRepository.findByCode(code);

        if (!optionalRole.isPresent()) {
            return;
        }

        RoleEntity role = optionalRole.get();

        if (role.getPermissionCodes() != null) {
            return;
        }

        role.setPermissionCodes(permissionCodes);
        role.setPermissionCount(permissionCodes.isEmpty() ? 0 : permissionCodes.split(",").length);
        roleRepository.save(role);
    }
}
