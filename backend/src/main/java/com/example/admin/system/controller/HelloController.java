package com.example.admin.system.controller;

import com.example.admin.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统模块的演示接口。
 *
 * @RestController 表示这个类专门提供接口数据，而不是返回 HTML 页面。
 * 后台管理系统一般就是这种模式：后端返回 JSON，前端负责页面展示。
 */
@RestController
@RequestMapping("/api/system")
public class HelloController {

    /**
     * 第一个后端接口。
     *
     * 请求地址：GET /api/system/hello
     * 用途：让前端验证是否已经成功连接到后端。
     */
    @GetMapping("/hello")
    public ApiResponse<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<String, Object>();

        // message 是展示给前端页面看的内容。
        data.put("message", "后端接口连接成功");

        // module 用来告诉前端这个接口属于哪个业务模块。
        data.put("module", "system");

        return ApiResponse.success(data);
    }
}
