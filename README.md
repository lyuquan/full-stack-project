# 前后端分离后台管理系统学习项目

这个项目会按步骤搭建一个后台管理系统，适合前端同学循序渐进学习 Java 后端。

## 项目结构

```text
full-stack-project
├─ backend   后端 Java Spring Boot 项目
└─ frontend  前端 Vue + Vite 项目
```

## 当前进度

### 第 1 步：跑通前后端分离

目标：让前端页面调用后端接口，理解“前端负责页面，后端负责数据”的基本关系。

接口：

```text
GET /api/system/hello
```

你需要理解：

- `Controller` 是后端接收浏览器请求的入口。
- 前端不直接操作数据库，而是请求后端接口。
- Vite 的 `proxy` 可以把前端的 `/api` 请求转发给后端，避免开发环境跨域问题。

### 第 2 步：用户列表接口和后端分层

目标：新增用户列表接口，理解后端常见的 `Controller -> Service -> VO` 分层。

接口：

```text
GET /api/users
```

本步骤新增的后端文件：

```text
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
backend/src/main/java/com/example/admin/user/vo/UserVO.java
```

分层含义：

- `Controller`：接收前端请求，返回接口结果。
- `Service`：处理业务逻辑，后续会在这里调用数据库。
- `VO`：View Object，表示返回给前端页面展示的数据结构。

当前用户数据还没有接数据库，而是在 `UserService` 里模拟出来。这样可以先把接口分层学清楚。

### 第 3 步：新增用户接口和 DTO

目标：新增一个用户表单，让前端通过 POST 请求把 JSON 提交给后端。

接口：

```text
POST /api/users
```

请求体示例：

```json
{
  "username": "zhangsan",
  "nickname": "张三",
  "role": "运营管理员",
  "status": "enabled"
}
```

本步骤新增的后端文件：

```text
backend/src/main/java/com/example/admin/user/dto/CreateUserDTO.java
```

本步骤修改的后端文件：

```text
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
```

你需要理解：

- `DTO`：Data Transfer Object，用来接收前端传给后端的请求参数。
- `VO`：View Object，用来控制后端返回给前端的数据结构。
- `@RequestBody`：告诉 Spring Boot 从请求体 JSON 里读取参数，并转换成 Java 对象。
- `POST`：通常用于新增数据；`GET` 通常用于查询数据。
- 当前新增用户只保存在内存列表里，重启后端后会恢复初始数据；后续接数据库后才会真正持久化。

### 第 4 步：接口参数校验

目标：让后端拦住无效请求，比如账号为空、昵称为空、状态值不合法。

本步骤新增或修改：

```text
backend/pom.xml
backend/src/main/java/com/example/admin/common/ApiResponse.java
backend/src/main/java/com/example/admin/common/GlobalExceptionHandler.java
backend/src/main/java/com/example/admin/user/dto/CreateUserDTO.java
backend/src/main/java/com/example/admin/user/controller/UserController.java
frontend/src/App.vue
```

你需要理解：

- `spring-boot-starter-validation`：Spring Boot 的参数校验依赖。
- `@Valid`：放在 Controller 参数上，触发 DTO 里的校验规则。
- `@NotBlank`：字符串不能是 null、空字符串、纯空格。
- `@Size`：限制字符串长度。
- `@Pattern`：用正则限制字段格式，比如状态只能是 `enabled` 或 `disabled`。
- `@RestControllerAdvice`：全局处理 Controller 抛出的异常。
- `@ExceptionHandler`：指定某类异常应该怎么处理。

校验失败时，后端会返回统一结构：

```json
{
  "code": 400,
  "message": "账号不能为空",
  "data": null
}
```

这一点很重要：前端校验只是提升体验，后端校验才是真正的兜底。

## 启动后端

进入后端目录：

```powershell
cd d:\full-stack-project\backend
.\run.ps1
```

如果你想看完整 Maven 命令，它等价于：

```powershell
..\.tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run -s .mvn\settings.xml
```

后端默认地址：

```text
http://localhost:8080
```

测试接口：

```text
http://localhost:8080/api/system/hello
http://localhost:8080/api/users
```

说明：

- `.mvn/settings.xml` 是当前项目专用 Maven 配置。
- 它用来避开本机全局 Maven 镜像配置问题。
- `.tools` 目录里是项目本地下载的 Maven 工具。

## 启动前端

进入前端目录：

```powershell
cd d:\full-stack-project\frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 代码生成约定

每次新增代码时，都会加清晰注释，说明这段代码的职责和用途，方便边写边学。
