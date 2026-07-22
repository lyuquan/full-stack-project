package com.example.admin.role.config;

import com.example.admin.role.entity.RoleEntity;
import com.example.admin.role.repository.RoleRepository;
import com.example.admin.user.constant.UserConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
            return;
        }

        roleRepository.saveAll(Arrays.asList(
                createRole(
                        "super_admin",
                        UserConstants.ROLE_SUPER_ADMIN,
                        "Owns user query, user write and role management permissions.",
                        3
                ),
                createRole(
                        "operator",
                        UserConstants.ROLE_OPERATOR,
                        "Can query user data but cannot change users or roles.",
                        1
                ),
                createRole(
                        "readonly",
                        UserConstants.ROLE_READONLY,
                        "Disabled demo account used to learn login status checks.",
                        0
                )
        ));
    }

    /**
     * Creates one RoleEntity object for initial demo data.
     */
    private RoleEntity createRole(String code, String name, String description, Integer permissionCount) {
        RoleEntity role = new RoleEntity();
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setPermissionCount(permissionCount);
        return role;
    }
}
