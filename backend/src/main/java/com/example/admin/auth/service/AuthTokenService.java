package com.example.admin.auth.service;

import com.example.admin.user.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 学习版 token 服务。
 *
 * 登录成功时生成 token，并把 token 和用户信息保存到服务端内存里。
 * 后续请求带 token 过来时，拦截器可以通过这里判断 token 是否真的由后端签发。
 */
@Service
public class AuthTokenService {

    /**
     * tokenStore 是内存版 token 仓库。
     *
     * key 是 token 字符串，value 是当前登录用户的简单信息。
     * ConcurrentHashMap 支持多线程安全读写，比普通 HashMap 更适合 Web 请求场景。
     */
    private final ConcurrentMap<String, LoginUser> tokenStore = new ConcurrentHashMap<String, LoginUser>();

    /**
     * 登录成功后创建 token。
     */
    public String createToken(UserEntity user) {
        String token = "study-token-" + user.getId() + "-" + UUID.randomUUID().toString();

        tokenStore.put(token, new LoginUser(user.getId(), user.getUsername(), user.getRole()));

        return token;
    }

    /**
     * 根据 token 查询当前登录用户。
     *
     * 返回 null 表示 token 不存在，通常就是未登录或 token 已失效。
     */
    public LoginUser getLoginUser(String token) {
        return tokenStore.get(token);
    }

    /**
     * 判断 token 是否有效。
     */
    public boolean isValid(String token) {
        return getLoginUser(token) != null;
    }

    /**
     * 保存在 token 仓库里的当前登录用户信息。
     *
     * 这里只保存接口校验常用的少量字段，不保存密码。
     */
    public static class LoginUser {

        private final Long id;

        private final String username;

        private final String role;

        public LoginUser(Long id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }
}
