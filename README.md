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

目标：让用户列表支持按关键字和状态筛选，学习后台系统里常见的“查询参数”接口。

接口：

```text
GET /api/users?keyword=admin&status=enabled
```

为什么要做这一步：

- 后台系统的列表页通常都要支持查询，比如按账号、昵称、状态筛选。
- 前端筛选条件一般不放到请求体里，而是放到 URL 查询参数里。
- 后端用 `@RequestParam` 接收查询参数，再传给 Service 和 Repository 查询数据库。

你需要理解：

- `@RequestParam`：从 URL 查询参数里取值，比如 `?keyword=admin`。
- `required = false`：表示这个参数可以不传。
- `URLSearchParams`：前端用来拼接查询参数，避免手写字符串出错。
- `@Query`：在 Repository 里自定义查询语句。
- `:keyword` 和 `:status`：查询语句里的命名参数，对应方法参数上的 `@Param`。
- `LIKE '%关键字%'`：数据库里的模糊查询，用来匹配包含关键字的账号或昵称。

### 第 9 步：用户列表分页查询

目标：让用户列表支持分页，理解真实后台列表页为什么不会一次性返回全部数据。

接口：

```text
GET /api/users?keyword=admin&status=enabled&page=1&size=5
```

为什么要做这一步：

- 如果用户有几万条，一次性返回全部数据会很慢，也会让前端页面卡顿。
- 分页可以让后端每次只返回当前页数据，比如第 1 页 5 条。
- 前端还需要知道总条数 `total`，这样才能显示“共多少条、当前第几页”。

每个文件的作用：

- `PageResult.java`：统一分页返回结构，里面有 `records`、`total`、`page`、`size`。
- `UserController.java`：给 `GET /api/users` 增加 `page` 和 `size` 参数，并返回分页结果。
- `UserService.java`：把前端页码转成 JPA 页码，调用 Repository 查询分页数据，再把 Entity 转成 VO。
- `UserRepository.java`：把返回类型从普通列表改成 `Page<UserEntity>`，让 JPA 同时返回当前页数据和总条数。
- `App.vue`：新增分页状态、每页数量选择、上一页、下一页，并解析后端返回的 `records/total/page/size`。
- `style.css`：新增分页条样式，让总条数和分页按钮在表格下面显示。
- `README.md`：记录第 9 步的学习目标、接口格式和每个文件的职责。

你需要理解：

- `PageResult<T>`：自己定义的分页返回对象，`T` 表示 records 里放什么类型的数据。
- `records`：当前页的数据列表。
- `total`：符合查询条件的总条数，不只是当前页条数。
- `page`：当前页码，我们给前端使用，从 1 开始。
- `size`：每页显示多少条。
- `Pageable`：Spring Data JPA 的分页参数对象。
- `PageRequest.of(page, size, sort)`：创建分页参数，告诉 JPA 查第几页、每页几条、按什么排序。
- `Page<UserEntity>`：JPA 的分页查询结果，既有当前页数据，也有总条数。
- 前端页码从 1 开始，但 JPA 页码从 0 开始，所以 Service 里要用 `safePage - 1`。

### 第 10 步：用户详情接口

目标：点击用户列表里的“查看”，请求后端详情接口，只查询一个用户。

接口：

```text
GET /api/users/{id}
```

请求示例：

```text
GET /api/users/1
```

为什么要做这一步：

- 列表接口通常只负责展示一批数据，详情接口负责展示某一条数据。
- 真实项目里，列表页字段可能比较少，详情页字段可能更多，所以两类接口会分开设计。
- 详情接口继续练习 `@PathVariable`，也能理解“通过 ID 查询单条记录”的后端流程。

每个文件的作用：

- `UserController.java`：新增 `GET /api/users/{id}`，接收 URL 里的用户 ID，查不到时返回 404。
- `UserService.java`：新增 `getUserById`，通过 Repository 的 `findById` 查询单个用户。
- `App.vue`：新增“查看”按钮、详情加载状态、详情错误提示、详情展示区域。
- `style.css`：新增详情区域样式，让详情用键值对展示。
- `README.md`：记录第 10 步的接口、原因和文件职责。

你需要理解：

- `/api/users`：用户列表接口，查询一批用户。
- `/api/users/{id}`：用户详情接口，只查询一个用户。
- `@GetMapping("/{id}")`：匹配 `/api/users/1` 这种路径。
- `@PathVariable Long id`：把路径里的 `1` 转成 Java 的 `Long id`。
- `findById(id)`：JPA 自带方法，用主键 ID 查一条数据。
- `Optional<UserEntity>`：表示“可能有值，也可能没值”，避免直接拿空对象导致报错。
- 查不到用户时返回 `ApiResponse.error(404, "用户不存在")`。

### 第 11 步：启用和禁用用户

目标：在用户列表里直接点击“启用”或“禁用”，只修改用户状态字段。

接口：

```text
PATCH /api/users/{id}/status
```

请求示例：

```text
PATCH /api/users/1/status
Content-Type: application/json

{
  "status": "disabled"
}
```

为什么要做这一步：

- 后台系统经常有启用、禁用、上架、下架这类“只改一个字段”的操作。
- 如果只改状态，不应该要求前端提交完整用户表单，否则容易误改账号、昵称、角色。
- `PATCH` 表示局部更新，比 `PUT` 更适合只修改 `status` 这种场景。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/dto/UpdateUserStatusDTO.java
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
frontend/src/App.vue
frontend/src/style.css
README.md
```

每个文件的作用：

- `UpdateUserStatusDTO.java`：只接收 `status` 字段，并校验只能是 `enabled` 或 `disabled`。
- `UserController.java`：新增 `PATCH /api/users/{id}/status`，专门处理启用和禁用。
- `UserService.java`：新增 `updateUserStatus`，只修改数据库里的 `status` 字段。
- `App.vue`：新增“启用/禁用”按钮，调用 PATCH 接口，成功后刷新列表和详情。
- `style.css`：给禁用状态下的普通操作按钮补充样式。
- `README.md`：记录第 11 步的接口、原因和文件职责。

你需要理解：

- `PUT`：通常表示整体更新，比如编辑完整用户信息。
- `PATCH`：通常表示局部更新，比如只改一个状态字段。
- 单独建 `UpdateUserStatusDTO` 是为了让接口参数更清楚，也避免前端传多余字段。
- 后端仍然要校验 `status`，不能只相信前端按钮传来的值。

### 第 12 步：账号唯一校验

目标：新增和编辑用户时，后端拦住重复账号，保证 `username` 不会重复。

为什么要做这一步：

- 账号通常是登录身份，不能让两个用户使用同一个账号。
- 前端可以做提示，但真正可靠的限制必须放在后端。
- 账号重复不是“字段为空”这种格式问题，而是需要查数据库后才能判断的业务规则。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/common/BusinessException.java
backend/src/main/java/com/example/admin/common/GlobalExceptionHandler.java
backend/src/main/java/com/example/admin/user/repository/UserRepository.java
backend/src/main/java/com/example/admin/user/service/UserService.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `BusinessException.java`：自定义业务异常，用来表达“请求格式正确，但业务规则不允许”。
- `GlobalExceptionHandler.java`：捕获 `BusinessException`，继续返回统一的 `ApiResponse` 结构。
- `UserRepository.java`：新增 `existsByUsername` 和 `existsByUsernameAndIdNot`，让 JPA 自动生成查询。
- `UserService.java`：新增和编辑保存前检查账号是否重复，重复时抛出 `账号已存在`。
- `App.vue`：账号输入框提示它是唯一账号；接口错误仍然通过 `userError` 展示。
- `README.md`：记录第 12 步的学习目标、原因和文件职责。

你需要理解：

- 参数校验：比如不能为空、长度不能超过 30，通常写在 DTO 注解上。
- 业务校验：比如账号不能重复，需要查数据库，通常写在 Service 里。
- `existsByUsername`：Spring Data JPA 根据方法名自动生成 SQL，判断账号是否存在。
- `existsByUsernameAndIdNot`：编辑时使用，表示“这个账号是否被其他用户使用”。
- `BusinessException`：Service 发现业务不允许时抛出，Controller 不需要到处手写重复判断。

### 第 13 步：创建时间和更新时间

目标：给用户数据增加 `createdAt` 和 `updatedAt`，让列表和详情能展示数据创建、最后修改时间。

为什么要做这一步：

- 后台系统经常需要知道一条数据什么时候创建、什么时候最后修改。
- 创建时间和更新时间属于通用审计字段，很多数据库表都会有。
- 这些字段不应该由前端传，而应该由后端在保存数据时自动生成。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/entity/UserEntity.java
backend/src/main/java/com/example/admin/user/vo/UserVO.java
backend/src/main/java/com/example/admin/user/service/UserService.java
frontend/src/App.vue
frontend/src/style.css
README.md
```

每个文件的作用：

- `UserEntity.java`：新增 `createdAt` 和 `updatedAt` 字段，并用 JPA 回调自动维护时间。
- `UserVO.java`：把创建时间和更新时间暴露给前端页面。
- `UserService.java`：在 `toVO` 里把 Entity 的时间字段转换到 VO。
- `App.vue`：详情展示创建时间和更新时间，列表展示更新时间。
- `style.css`：表格多了一列，所以增加最小宽度，避免内容挤压。
- `README.md`：记录第 13 步的学习目标和文件职责。

你需要理解：

- `LocalDateTime`：Java 里常用的日期时间类型。
- `@Column(nullable = false)`：数据库字段不能为空。
- `@PrePersist`：JPA 在新增保存前自动调用，适合设置创建时间。
- `@PreUpdate`：JPA 在更新保存前自动调用，适合刷新更新时间。
- `createdAt`：创建后一般不再改变。
- `updatedAt`：每次编辑、启用禁用等更新操作后都会变化。

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
http://localhost:8080/api/users?page=1&size=5
http://localhost:8080/api/users/1
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
