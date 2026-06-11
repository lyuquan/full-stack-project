package com.example.admin.user.controller;

import com.example.admin.common.ApiResponse;
import com.example.admin.user.service.UserService;
import com.example.admin.user.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理接口控制器。
 *
 * Controller 层负责接收前端请求，并把请求转交给 Service 层处理。
 * 它不直接关心数据怎么来，只负责把结果包装成统一接口格式返回给前端。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 用户业务服务。
     * Spring 会根据 @Service 自动创建 UserService 对象，并通过构造方法传进来。
     */
    private final UserService userService;

    /**
     * 构造方法注入。
     *
     * 这是 Spring 推荐的依赖注入方式：需要什么依赖，就通过构造方法声明出来。
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询用户列表接口。
     *
     * 请求地址：GET /api/users
     * 返回数据：统一响应对象，data 里面是用户 VO 列表。
     */
    @GetMapping
    public ApiResponse<List<UserVO>> listUsers() {
        List<UserVO> users = userService.listUsers();

        return ApiResponse.success(users);
    }
}
