package com.example.admin.auth.service;

import com.example.admin.auth.dto.LoginDTO;
import com.example.admin.auth.constant.AuthPermissions;
import com.example.admin.auth.vo.LoginVO;
import com.example.admin.auth.vo.MenuVO;
import com.example.admin.auth.vo.PermissionVO;
import com.example.admin.common.BusinessException;
import com.example.admin.user.constant.UserConstants;
import com.example.admin.user.entity.UserEntity;
import com.example.admin.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 登录认证业务服务。
 *
 * Controller 只负责接收请求，真正的账号查询、密码校验、账号状态判断和 token 生成都放在这里。
 */
@Service
public class AuthService {

    /**
     * 用户数据库访问对象，用来根据账号查询用户。
     */
    private final UserRepository userRepository;

    /**
     * BCrypt 密码工具，用来校验用户输入的明文密码和数据库里的密文是否匹配。
     */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 学习版 token 服务，负责生成 token 并保存到服务端内存。
     */
    private final AuthTokenService authTokenService;

    public AuthService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            AuthTokenService authTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
    }

    /**
     * 登录方法。
     *
     * 前端传过来的是明文密码，数据库保存的是 BCrypt 密文。
     * 校验通过后，后端生成 token 并返回给前端。
     */
    public LoginVO login(LoginDTO loginDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(loginDTO.getUsername());

        if (!optionalUser.isPresent()) {
            throw new BusinessException(400, "账号或密码错误");
        }

        UserEntity user = optionalUser.get();

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "账号或密码错误");
        }

        if (UserConstants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(400, "账号已被禁用");
        }

        List<String> permissions = AuthPermissions.listByRole(user.getRole());

        return new LoginVO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole(),
                permissions,
                AuthPermissions.canManageUsers(permissions),
                authTokenService.createToken(user)
        );
    }

    /**
     * Build sidebar menus from permission codes.
     *
     * The frontend no longer needs to hard-code permission rules for menus.
     */
    public List<MenuVO> listMenus(List<String> permissions) {
        List<MenuVO> menus = new ArrayList<MenuVO>();

        menus.add(new MenuVO("home", "系统首页", "/", true));

        if (permissions != null && permissions.contains(AuthPermissions.USER_READ)) {
            menus.add(new MenuVO("users", "用户管理", "/users", false));
        }

        if (permissions != null && permissions.contains(AuthPermissions.ROLE_MANAGE)) {
            menus.add(new MenuVO("roles", "角色管理", "/roles", false));
        }

        return menus;
    }

    /**
     * Query all permission definitions used by this learning project.
     *
     * For now permissions are a code dictionary in Java. Later we can move
     * them into database tables such as sys_permission and sys_role_permission.
     */
    public List<PermissionVO> listPermissions() {
        List<PermissionVO> permissions = new ArrayList<PermissionVO>();

        // Each permission code here must also exist in AuthPermissions.listAllCodes().
        permissions.add(new PermissionVO(
                AuthPermissions.USER_READ,
                "查看用户",
                "允许查看用户列表、详情和统计数据"
        ));
        permissions.add(new PermissionVO(
                AuthPermissions.USER_WRITE,
                "管理用户",
                "允许新增、编辑、启用、禁用和删除用户"
        ));
        permissions.add(new PermissionVO(
                AuthPermissions.ROLE_MANAGE,
                "管理角色",
                "允许查看角色管理菜单并维护角色数据"
        ));

        return permissions;
    }
}
