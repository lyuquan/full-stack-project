package com.example.admin.user.service;

import com.example.admin.user.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户业务服务。
 *
 * Service 层负责处理业务逻辑，Controller 不直接拼业务数据。
 * 当前还没有接数据库，所以这里先返回模拟用户列表。
 */
@Service
public class UserService {

    /**
     * 查询用户列表。
     *
     * 现在的数据写死在代码里，是为了先学习 Controller -> Service -> VO 的流转。
     * 后续接 MySQL 后，这个方法会改成从数据库查询用户。
     */
    public List<UserVO> listUsers() {
        List<UserVO> users = new ArrayList<UserVO>();

        users.add(new UserVO(1L, "admin", "系统管理员", "超级管理员", "enabled"));
        users.add(new UserVO(2L, "manager", "运营经理", "运营管理员", "enabled"));
        users.add(new UserVO(3L, "auditor", "审计专员", "只读用户", "disabled"));

        return users;
    }
}
