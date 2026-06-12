package com.example.admin.user.controller;

import com.example.admin.common.ApiResponse;
import com.example.admin.user.dto.CreateUserDTO;
import com.example.admin.user.service.UserService;
import com.example.admin.user.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
     */
    @GetMapping
    public ApiResponse<List<UserVO>> listUsers() {
        List<UserVO> users = userService.listUsers();

        return ApiResponse.success(users);
    }

    /**
     * Create user.
     *
     * Request: POST /api/users
     * @RequestBody reads JSON from the request body.
     * @Valid triggers validation annotations in CreateUserDTO.
     */
    @PostMapping
    public ApiResponse<UserVO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        UserVO user = userService.createUser(createUserDTO);

        return ApiResponse.success(user);
    }
}
