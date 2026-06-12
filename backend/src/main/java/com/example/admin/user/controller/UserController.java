package com.example.admin.user.controller;

import com.example.admin.common.ApiResponse;
import com.example.admin.user.dto.CreateUserDTO;
import com.example.admin.user.dto.UpdateUserDTO;
import com.example.admin.user.service.UserService;
import com.example.admin.user.vo.UserVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
 * User management API controller.
 *
 * Controller receives frontend requests and delegates business work to Service.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * User business service injected by Spring.
     */
    private final UserService userService;

    /**
     * Constructor injection makes required dependencies explicit.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Query user list.
     *
     * Request: GET /api/users
     * Request with filters: GET /api/users?keyword=admin&status=enabled
     */
    @GetMapping
    public ApiResponse<List<UserVO>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        List<UserVO> users = userService.listUsers(keyword, status);

        return ApiResponse.success(users);
    }

    /**
     * Create user.
     *
     * Request: POST /api/users
     */
    @PostMapping
    public ApiResponse<UserVO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        UserVO user = userService.createUser(createUserDTO);

        return ApiResponse.success(user);
    }

    /**
     * Update user.
     *
     * Request: PUT /api/users/{id}
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
     * Delete user.
     *
     * Request: DELETE /api/users/{id}
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
