# 前后端分离后台管理系统学习项目

这个项目会按步骤搭建一个后台管理系统，适合前端同学循序渐进学习 Java 后端。

## 项目结构

```text
full-stack-project
├── backend   后端 Java Spring Boot 项目
└── frontend  前端 Vue + Vite 项目
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

分层含义：

- `Controller`：接收前端请求，返回接口结果。
- `Service`：处理业务逻辑，后续会在这里调用数据库。
- `VO`：View Object，表示返回给前端页面展示的数据结构。

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

你需要理解：

- `DTO`：Data Transfer Object，用来接收前端传给后端的请求参数。
- `VO`：View Object，用来控制后端返回给前端的数据结构。
- `@RequestBody`：告诉 Spring Boot 从请求体 JSON 里读取参数，并转换成 Java 对象。
- `POST`：通常用于新增数据，`GET` 通常用于查询数据。

### 第 4 步：接口参数校验

目标：让后端拦住无效请求，比如账号为空、昵称为空、状态值不合法。

你需要理解：

- `spring-boot-starter-validation`：Spring Boot 的参数校验依赖。
- `@Valid`：放在 Controller 参数上，触发 DTO 里的校验规则。
- `@NotBlank`：字符串不能是 `null`、空字符串、纯空格。
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

### 第 5 步：编辑用户接口

目标：给用户列表增加“编辑”能力，前端点击编辑后回填表单，提交时调用后端更新接口。

接口：

```text
PUT /api/users/{id}
```

请求示例：

```text
PUT /api/users/1
```

你需要理解：

- `PUT`：通常用于修改一条已有数据。
- `@PathVariable`：从 URL 路径里读取变量，比如 `/api/users/1` 里的 `1`。
- `UpdateUserDTO`：编辑用户时的请求参数对象。
- 前端编辑模式：点击表格“编辑”后，把当前行数据填入表单，再提交 `PUT` 请求。

### 第 6 步：删除用户接口

目标：给用户列表增加“删除”能力，理解 REST 风格里的 `DELETE` 请求。

接口：

```text
DELETE /api/users/{id}
```

请求示例：

```text
DELETE /api/users/1
```

你需要理解：

- `DELETE`：通常用于删除一条已有数据。
- `@DeleteMapping("/{id}")`：声明一个删除接口，并从 URL 里接收用户 ID。
- `Service` 里的 `deleteUser`：负责真正删除用户。
- 前端删除流程：点击删除、弹出确认框、发送 `DELETE` 请求、成功后刷新列表。

### 第 7 步：接入 H2 数据库和 Repository 层

目标：把用户数据从内存列表改成数据库表，让新增、编辑、删除后的数据在后端重启后仍然保留。

新增或修改：

```text
backend/pom.xml
backend/src/main/resources/application.yml
backend/src/main/java/com/example/admin/user/config/UserDataInitializer.java
backend/src/main/java/com/example/admin/user/entity/UserEntity.java
backend/src/main/java/com/example/admin/user/repository/UserRepository.java
backend/src/main/java/com/example/admin/user/service/UserService.java
backend/src/main/java/com/example/admin/user/vo/UserVO.java
```

新的后端分层：

```text
Controller -> Service -> Repository -> Database
```

你需要理解：

- `Entity`：数据库表对应的 Java 对象，比如 `UserEntity` 对应 `sys_user` 表。
- `@Entity`：告诉 JPA 这个类是一张数据库表的映射。
- `@Table(name = "sys_user")`：指定数据库表名。
- `@Id`：声明主键字段。
- `@GeneratedValue`：声明 ID 由数据库生成。
- `Repository`：数据库访问层，负责查库、保存、删除。
- `JpaRepository<UserEntity, Long>`：第一个泛型是表对应的 Entity，第二个泛型是主键 ID 类型。
- `CommandLineRunner`：Spring Boot 启动完成后自动执行的初始化逻辑。
- `UserDataInitializer`：当用户表为空时，自动插入 3 条演示用户。
- H2 文件数据库会生成在 `backend/data` 目录下，重启后端后数据仍然保留。

H2 控制台：

```text
http://localhost:8080/h2-console
```

连接信息：

```text
JDBC URL: jdbc:h2:file:./data/admin
User Name: sa
Password: 留空
```

### 第 8 步：用户查询筛选

目标：让用户列表支持按关键字和状态筛选，学习后台系统里最常见的“查询参数”接口。

接口：

```text
GET /api/users?keyword=admin&status=enabled
```

为什么要做这一步：

- 后台系统的列表页通常都要支持查询，比如按账号、昵称、状态筛选。
- 前端筛选条件一般不会放到请求体里，而是放到 URL 查询参数里。
- 后端用 `@RequestParam` 接收查询参数，再传给 Service 和 Repository 查数据库。

这一步修改了什么：

```text
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
backend/src/main/java/com/example/admin/user/repository/UserRepository.java
frontend/src/App.vue
frontend/src/style.css
README.md
```

每个文件的作用：

- `UserController.java`：给 `GET /api/users` 增加 `keyword` 和 `status` 两个可选查询参数。
- `UserService.java`：把查询条件传给 Repository，并把数据库 Entity 转成前端 VO。
- `UserRepository.java`：新增 `searchUsers`，真正负责按账号、昵称、状态查数据库。
- `App.vue`：新增筛选表单，把关键字和状态拼成 URL 查询参数。
- `style.css`：给筛选表单补布局样式。
- `README.md`：记录第 8 步的学习目标和文件职责。

你需要理解：

- `@RequestParam`：从 URL 查询参数里取值，比如 `?keyword=admin`。
- `required = false`：表示这个参数可以不传。
- `URLSearchParams`：前端用来拼接查询参数，避免手写字符串出错。
- `@Query`：在 Repository 里自定义查询语句。
- `:keyword` 和 `:status`：查询语句里的命名参数，对应方法参数上的 `@Param`。
- `LIKE '%关键字%'`：数据库里的模糊查询，用来匹配包含关键字的账号或昵称。

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
