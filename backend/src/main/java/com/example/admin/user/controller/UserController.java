package com.example.admin.user.controller;

import com.example.admin.common.ApiResponse;
import com.example.admin.common.PageResult;
import com.example.admin.user.dto.CreateUserDTO;
import com.example.admin.user.dto.UpdateUserDTO;
import com.example.admin.user.dto.UpdateUserStatusDTO;
import com.example.admin.user.service.UserService;
import com.example.admin.user.vo.OptionVO;
import com.example.admin.user.vo.UserStatsVO;
import com.example.admin.user.vo.UserVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理接口入口。
 *
 * Controller 负责接收前端请求、读取 URL 参数或请求体，然后调用 Service 处理业务。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 用户业务服务，由 Spring 自动注入。
     */
    private final UserService userService;

    /**
     * 构造方法注入：让这个 Controller 必须依赖 UserService 这件事更明确。
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表。
     *
     * 示例：GET /api/users?keyword=admin&role=运营管理员&status=enabled&page=1&size=5
     */
    @GetMapping
    public ApiResponse<PageResult<UserVO>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        PageResult<UserVO> result = userService.listUsers(keyword, role, status, page, size);

        return ApiResponse.success(result);
    }

    /**
     * 查询用户统计数据。
     *
     * 这个接口必须放在 /{id} 详情接口前面，避免 /stats 被当成用户 ID。
     * 示例：GET /api/users/stats
     */
    @GetMapping("/stats")
    public ApiResponse<UserStatsVO> getUserStats() {
        UserStatsVO stats = userService.getUserStats();

        return ApiResponse.success(stats);
    }

    /**
     * 查询用户角色下拉选项。
     *
     * 这个接口给前端筛选表单和用户表单使用，避免前端把角色选项写死。
     * 示例：GET /api/users/roles
     */
    @GetMapping("/roles")
    public ApiResponse<List<OptionVO>> listRoleOptions() {
        List<OptionVO> roles = userService.listRoleOptions();

        return ApiResponse.success(roles);
    }

    /**
     * 查询用户状态下拉选项。
     *
     * 这个接口给前端筛选表单、用户表单和状态展示逻辑使用。
     * 示例：GET /api/users/statuses
     */
    @GetMapping("/statuses")
    public ApiResponse<List<OptionVO>> listStatusOptions() {
        List<OptionVO> statuses = userService.listStatusOptions();

        return ApiResponse.success(statuses);
    }

    /**
     * 查询单个用户详情。
     *
     * 示例：GET /api/users/1
     */
    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUserDetail(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);

        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        return ApiResponse.success(user);
    }

    /**
     * 新增用户。
     *
     * @RequestBody 表示从请求体 JSON 中读取字段，@Valid 表示触发 DTO 上的参数校验。
     */
    @PostMapping
    public ApiResponse<UserVO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        UserVO user = userService.createUser(createUserDTO);

        return ApiResponse.success(user);
    }

    /**
     * 编辑用户。
     *
     * @PathVariable 会读取 /api/users/{id} 里的 id，例如 /api/users/1 中的 1。
     */
    @PutMapping("/{id}")
    public ApiResponse<UserVO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO
    ) {
        UserVO user = userService.updateUser(id, updateUserDTO);

        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        return ApiResponse.success(user);
    }

    /**
     * 启用或禁用用户。
     *
     * PATCH 表示局部更新，这里只修改 status 字段。
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<UserVO> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusDTO updateUserStatusDTO
    ) {
        UserVO user = userService.updateUserStatus(id, updateUserStatusDTO.getStatus());

        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        return ApiResponse.success(user);
    }

    /**
     * 删除用户。
     *
     * 删除成功不需要返回具体业务数据，所以 data 返回 null。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return ApiResponse.error(404, "用户不存在");
        }

        return ApiResponse.success(null);
    }
}
