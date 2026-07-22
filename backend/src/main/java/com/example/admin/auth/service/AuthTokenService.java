package com.example.admin.auth.service;

import com.example.admin.user.entity.UserEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 学习版 token 服务。
 *
 * 登录成功时生成 token，并把 token 和用户信息保存到服务端内存里。
 * 后续请求带 token 过来时，后端可以通过这里判断 token 是否真的有效。
 */
@Service
public class AuthTokenService {

    /**
     * Authorization 是 HTTP 里常用来放登录凭证的请求头名称。
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer 是 token 请求头的常见格式前缀。
     */
    private static final String BEARER_PREFIX = "Bearer ";

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

        tokenStore.put(token, new LoginUser(user.getId(), user.getUsername(), user.getNickname(), user.getRole()));

        return token;
    }

    /**
     * 从请求头里读取 token。
     *
     * 前端发送的是 "Bearer xxxxx"，后端真正要使用的是 xxxxx 这一段。
     */
    public String getToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorization.substring(BEARER_PREFIX.length());
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
     * 根据当前请求查询登录用户。
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);

        if (token == null) {
            return null;
        }

        return getLoginUser(token);
    }

    /**
     * 判断 token 是否有效。
     */
    public boolean isValid(String token) {
        return getLoginUser(token) != null;
    }

    /**
     * 让当前请求携带的 token 失效。
     *
     * 退出登录时调用这个方法，后端会从 token 仓库里删除这个 token。
     */
    public void removeToken(HttpServletRequest request) {
        String token = getToken(request);

        if (token != null) {
            tokenStore.remove(token);
        }
    }

    /**
     * 保存在 token 仓库里的当前登录用户信息。
     *
     * 这里只保存接口校验常用的少量字段，不保存密码。
     */
    public static class LoginUser {

        private final Long id;

        private final String username;

        private final String nickname;

        private final String role;

        public LoginUser(Long id, String username, String nickname, String role) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getNickname() {
            return nickname;
        }

        public String getRole() {
            return role;
        }
    }
}
