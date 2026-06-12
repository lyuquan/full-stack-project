package com.example.admin.user.service;

import com.example.admin.user.dto.CreateUserDTO;
import com.example.admin.user.dto.UpdateUserDTO;
import com.example.admin.user.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户业务服务。
 *
 * Service 层负责处理业务逻辑，Controller 不直接拼业务数据。
 * 当前还没有接数据库，所以这里先用内存列表模拟用户表。
 */
@Service
public class UserService {

    /**
     * 模拟数据库里的用户表。
     *
     * static 表示这份列表属于类本身，后端运行期间会一直保存在内存里。
     * 注意：现在只是为了学习 POST 新增流程；重启后端后，内存数据会恢复初始值。
     */
    private static final List<UserVO> USERS = new ArrayList<UserVO>();

    /**
     * 下一个用户 ID。
     *
     * 真实项目里 ID 通常由数据库自动生成；这里先用变量模拟自增 ID。
     */
    private static Long nextId = 4L;

    static {
        USERS.add(new UserVO(1L, "admin", "系统管理员", "超级管理员", "enabled"));
        USERS.add(new UserVO(2L, "manager", "运营经理", "运营管理员", "enabled"));
        USERS.add(new UserVO(3L, "auditor", "审计专员", "只读用户", "disabled"));
    }

    /**
     * 查询用户列表。
     *
     * 这里返回一个新的 ArrayList，避免外部代码直接修改 USERS 本身。
     */
    public List<UserVO> listUsers() {
        return new ArrayList<UserVO>(USERS);
    }

    /**
     * 新增用户。
     *
     * Controller 负责接收请求，Service 负责把请求参数转换成业务数据。
     */
    public UserVO createUser(CreateUserDTO createUserDTO) {
        UserVO user = new UserVO(
                nextId,
                createUserDTO.getUsername(),
                createUserDTO.getNickname(),
                createUserDTO.getRole(),
                createUserDTO.getStatus()
        );

        USERS.add(user);
        nextId = nextId + 1;

        return user;
    }

    /**
     * Update an existing user by ID.
     *
     * The current step still uses the in-memory list. Later, this method will
     * become an UPDATE statement in the database.
     */
    public UserVO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        for (UserVO user : USERS) {
            if (user.getId().equals(id)) {
                user.setUsername(updateUserDTO.getUsername());
                user.setNickname(updateUserDTO.getNickname());
                user.setRole(updateUserDTO.getRole());
                user.setStatus(updateUserDTO.getStatus());

                return user;
            }
        }

        return null;
    }
}
