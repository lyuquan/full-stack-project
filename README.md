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

### 第 14 步：按角色筛选用户

目标：让用户列表支持按角色筛选，理解一个新的查询条件如何从前端一路传到后端数据库查询。

接口：

```text
GET /api/users?keyword=admin&role=运营管理员&status=enabled&page=1&size=5
```

为什么要做这一步：

- 后台系统的列表页通常不只按一个条件查，而是多个条件组合查询。
- `role` 是用户表里已经存在的字段，所以不需要改数据库结构，只需要把它作为查询条件传下去。
- 新增查询条件时，前端、Controller、Service、Repository 必须保持参数名和含义一致。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
backend/src/main/java/com/example/admin/user/repository/UserRepository.java
frontend/src/App.vue
frontend/src/style.css
README.md
```

每个文件的作用：

- `UserController.java`：给 `GET /api/users` 增加 `role` 查询参数，接收前端 URL 里的 `role=运营管理员`。
- `UserService.java`：把 `role` 参数继续传给 Repository，保持 Controller 不直接操作数据库。
- `UserRepository.java`：在 JPQL 查询里增加 `and (:role is null or :role = '' or u.role = :role)`，表示角色为空时不过滤，有值时按角色精确匹配。
- `App.vue`：筛选表单新增“角色”下拉框，并在请求用户列表时把 `role` 拼进 `URLSearchParams`。
- `style.css`：筛选表单多了一个下拉框，所以调整网格列宽，避免按钮和输入框挤在一起。
- `README.md`：记录第 14 步的学习目标、接口格式和每个文件职责。

你需要理解：

- `@RequestParam(required = false) String role`：从 URL 查询参数里接收 `role`，不传也可以。
- `URLSearchParams`：前端拼查询参数的工具，会把中文角色名自动编码到 URL 里。
- `@Param("role")`：把 Java 方法参数绑定到 JPQL 里的 `:role`。
- `u.role = :role`：数据库查询条件，表示只查角色等于传入角色的用户。
- `:role is null or :role = ''`：当角色参数为空时，不启用角色筛选。

### 第 15 步：用户统计接口

目标：新增一个用户统计接口，在页面上展示用户总数、启用数、禁用数和各角色人数。

接口：

```text
GET /api/users/stats
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 10,
    "enabledCount": 8,
    "disabledCount": 2,
    "superAdminCount": 1,
    "operatorCount": 7,
    "readonlyCount": 2
  }
}
```

为什么要做这一步：

- 后台首页和管理页经常需要展示统计数字，不能只看当前分页列表。
- 统计接口通常单独设计，因为它返回的是汇总数据，不是一条用户详情，也不是用户列表。
- 这一节可以学习 Spring Data JPA 的 `countBy...` 方法命名规则。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/vo/UserStatsVO.java
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
backend/src/main/java/com/example/admin/user/repository/UserRepository.java
frontend/src/App.vue
frontend/src/style.css
README.md
```

每个文件的作用：

- `UserStatsVO.java`：定义统计接口返回给前端的数据结构，比如 `totalCount`、`enabledCount`。
- `UserController.java`：新增 `GET /api/users/stats` 接口入口，返回统一的 `ApiResponse<UserStatsVO>`。
- `UserService.java`：调用 Repository 的计数方法，组装完整统计结果。
- `UserRepository.java`：新增 `countByStatus` 和 `countByRole`，让 JPA 根据方法名自动生成计数 SQL。
- `App.vue`：新增统计接口请求、统计加载状态、统计错误提示和统计卡片展示。
- `style.css`：新增统计卡片布局，让统计数字在页面上更清晰。
- `README.md`：记录第 15 步的学习目标、接口格式和每个文件职责。

你需要理解：

- `count()`：`JpaRepository` 自带方法，用来统计整张表总条数。
- `countByStatus("enabled")`：Spring Data JPA 根据方法名生成 `where status = ?` 的计数 SQL。
- `countByRole("运营管理员")`：根据角色字段统计人数。
- `UserStatsVO`：专门给统计接口使用的 VO，不代表单个用户。
- `/api/users/stats` 要写在 `/api/users/{id}` 前面，避免 `stats` 被当成用户 ID。
- 统计数据和分页列表分开请求，分页只影响表格，不影响总数统计。

### 第 16 步：角色字段参数校验

目标：限制新增和编辑用户时的 `role` 字段，只允许提交当前系统支持的三个角色。

允许的角色：

```text
超级管理员
运营管理员
只读用户
```

为什么要做这一步：

- 前端下拉框只能提升用户体验，不能当成安全校验。
- 别人可以用 Postman、curl 或浏览器控制台直接调用接口，传入页面上不存在的角色。
- 后端必须对关键字段做兜底校验，保证数据库里不会保存无效业务数据。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/dto/CreateUserDTO.java
backend/src/main/java/com/example/admin/user/dto/UpdateUserDTO.java
README.md
```

每个文件的作用：

- `CreateUserDTO.java`：给新增用户请求里的 `role` 字段加 `@Pattern`，限制只能是三个合法角色之一。
- `UpdateUserDTO.java`：给编辑用户请求里的 `role` 字段加同样规则，避免编辑时改成非法角色。
- `README.md`：记录第 16 步的学习目标、原因和文件职责。

你需要理解：

- `@NotBlank`：只负责校验不能为空，但不限制具体内容。
- `@Pattern`：用正则表达式限制字段值必须匹配指定格式。
- `超级管理员|运营管理员|只读用户`：正则里的 `|` 表示“或者”，也就是三个值任选一个。
- `@Valid`：Controller 参数上有这个注解时，DTO 里的校验注解才会生效。
- 校验失败会被 `GlobalExceptionHandler` 捕获，并统一返回 `ApiResponse.error(400, message)`。

### 第 17 步：角色选项接口

目标：新增角色下拉选项接口，让前端从后端获取角色列表，而不是把角色选项写死在 Vue 页面里。

接口：

```text
GET /api/users/roles
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "value": "超级管理员",
      "label": "超级管理员"
    },
    {
      "value": "运营管理员",
      "label": "运营管理员"
    },
    {
      "value": "只读用户",
      "label": "只读用户"
    }
  ]
}
```

为什么要做这一步：

- 后台系统里常见很多下拉选项，比如角色、状态、部门、分类，这类接口通常叫“字典接口”或“选项接口”。
- 前端不应该到处写死业务选项，否则后端规则变了，前端也要到处找地方改。
- 当前角色仍然先写在 Java 代码里，后面学习“角色管理”时，可以再改成从数据库角色表读取。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/vo/OptionVO.java
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `OptionVO.java`：定义通用下拉选项返回结构，`value` 是提交给后端的真实值，`label` 是页面显示文字。
- `UserController.java`：新增 `GET /api/users/roles` 接口，返回角色选项列表。
- `UserService.java`：新增 `listRoleOptions`，先用固定列表返回三个角色。
- `App.vue`：新增 `loadRoleOptions` 请求角色选项，并用 `v-for` 渲染筛选表单和用户表单的角色下拉框。
- `README.md`：记录第 17 步的接口、原因和文件职责。

你需要理解：

- 选项接口：专门给前端下拉框、单选框等控件提供可选值的接口。
- `value`：真正提交给后端和保存到数据库的值。
- `label`：给用户看的显示文案。
- `v-for="role in roleOptions"`：Vue 根据接口返回的数组循环渲染 `<option>`。
- `/api/users/roles` 也要写在 `/api/users/{id}` 前面，避免 `roles` 被当成用户 ID。

### 第 18 步：状态选项接口

目标：新增状态下拉选项接口，让前端从后端获取 `enabled/disabled` 对应的显示文案。

接口：

```text
GET /api/users/statuses
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "value": "enabled",
      "label": "启用"
    },
    {
      "value": "disabled",
      "label": "禁用"
    }
  ]
}
```

为什么要做这一步：

- 角色和状态本质上都是下拉选项，都可以使用统一的 `value/label` 结构。
- 前端表单里不再写死 `enabled -> 启用`、`disabled -> 禁用` 的选项。
- 后端仍然保存稳定的英文值，前端展示中文文案，这样数据库值和页面文案职责更清楚。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/vo/OptionVO.java
backend/src/main/java/com/example/admin/user/controller/UserController.java
backend/src/main/java/com/example/admin/user/service/UserService.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `OptionVO.java`：由原来的角色选项 VO 泛化成通用选项 VO，角色和状态都能复用。
- `UserController.java`：新增 `GET /api/users/statuses` 接口，返回状态选项列表。
- `UserService.java`：新增 `listStatusOptions`，返回 `enabled/disabled` 两个状态。
- `App.vue`：新增 `loadStatusOptions`，筛选表单和用户表单的状态下拉框都改成接口返回；状态展示通过 `getStatusLabel` 转成中文。
- `README.md`：记录第 18 步的接口、原因和文件职责。

你需要理解：

- 通用 VO：如果两个接口返回结构一样，就可以共用一个 VO，减少重复类。
- `enabled/disabled`：后端和数据库使用的稳定状态值。
- `启用/禁用`：前端页面给用户看的显示文案。
- `getStatusLabel(status)`：前端根据接口返回的选项，把状态值转换成显示文案。
- `/api/users/statuses` 也要写在 `/api/users/{id}` 前面，避免 `statuses` 被当成用户 ID。

### 第 19 步：后端常量集中管理

目标：把用户模块里的角色值、状态值、校验正则和状态显示文案集中到一个常量类里。

为什么要做这一步：

- 之前 `超级管理员`、`运营管理员`、`enabled`、`disabled` 这些字符串分散在 DTO、Service 和选项接口里。
- 字符串重复越多，以后修改时越容易漏改，导致校验、统计和下拉选项不一致。
- 常量类可以让这些固定业务值有一个统一来源，后续改动更安全。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/user/constant/UserConstants.java
backend/src/main/java/com/example/admin/user/dto/CreateUserDTO.java
backend/src/main/java/com/example/admin/user/dto/UpdateUserDTO.java
backend/src/main/java/com/example/admin/user/dto/UpdateUserStatusDTO.java
backend/src/main/java/com/example/admin/user/config/UserDataInitializer.java
backend/src/main/java/com/example/admin/user/service/UserService.java
README.md
```

每个文件的作用：

- `UserConstants.java`：集中定义用户模块用到的固定角色、固定状态、校验正则和状态显示文案。
- `CreateUserDTO.java`：角色和状态校验正则改成引用 `UserConstants`。
- `UpdateUserDTO.java`：编辑用户时的角色和状态校验也引用同一份常量。
- `UpdateUserStatusDTO.java`：启用/禁用接口的状态校验引用同一份状态正则。
- `UserDataInitializer.java`：演示用户的初始角色和初始状态改成引用常量。
- `UserService.java`：统计查询和选项接口不再手写字符串，而是引用常量。
- `README.md`：记录第 19 步的学习目标、原因和文件职责。

你需要理解：

- `public static final`：Java 常量写法，表示这个值属于类本身，并且不可修改。
- `final class`：表示这个常量类不应该被继承。
- `private UserConstants()`：禁止别人 new 一个常量类对象，因为常量类只用类名访问。
- 注解里的 `regexp` 必须是编译期常量，所以 `UserConstants.ROLE_PATTERN` 可以用在 `@Pattern` 里。
- 常量集中管理不是新功能，而是为了降低后续维护成本。

### 第 20 步：后端接口测试

目标：给用户模块补充自动化接口测试，学习不用打开浏览器也能验证后端接口是否正常。

为什么要做这一步：

- 后台系统接口越来越多，只靠手动点页面测试很容易漏掉问题。
- 自动化测试可以在改代码后快速检查核心接口有没有被改坏。
- 这一步先测试已经做过的统计接口、角色选项接口、状态选项接口，让你理解后端测试的基本写法。

这一步新增或修改了什么：

```text
backend/src/test/java/com/example/admin/user/controller/UserControllerTest.java
README.md
```

每个文件的作用：

- `UserControllerTest.java`：用户 Controller 的接口测试类，用代码模拟请求后端接口，并检查返回的 JSON 是否符合预期。
- `README.md`：记录第 20 步的学习目标、测试命令和每段测试代码的含义。

你需要理解：

- `src/test/java`：专门放测试代码的目录，测试代码不会打包进正式后端程序。
- `@SpringBootTest`：启动 Spring 的测试环境，让 Controller、Service、Repository 这些 Bean 都能被加载。
- `@AutoConfigureMockMvc`：自动配置 `MockMvc`，让测试代码可以模拟 HTTP 请求。
- `@TestPropertySource`：给测试单独指定配置，这里使用 H2 内存数据库，避免测试影响 `backend/data/admin.mv.db` 里的开发数据。
- `MockMvc`：Spring 提供的接口测试工具，可以在不启动真实浏览器的情况下请求 Controller。
- `mockMvc.perform(get("/api/users/stats"))`：模拟发送一次 `GET /api/users/stats` 请求。
- `.andExpect(status().isOk())`：断言 HTTP 状态码必须是 200。
- `jsonPath("$.code", is(200))`：断言返回 JSON 里的 `code` 字段必须等于 200。
- `jsonPath("$.data.totalCount", is(3))`：断言返回 JSON 里的 `data.totalCount` 必须等于 3。
- `hasSize(3)`：断言返回数组长度必须是 3，比如角色选项有 3 个。

运行测试命令：

```powershell
cd d:\full-stack-project\backend
..\.tools\apache-maven-3.9.6\bin\mvn.cmd test -s .mvn\settings.xml
```

这一步测试的接口：

```text
GET /api/users/stats
GET /api/users/roles
GET /api/users/statuses
```

### 第 21 步：前端 API 请求封装

目标：把 `App.vue` 里重复的 `fetch -> response.json() -> 判断 code -> 取 data` 逻辑抽到单独的 `api.js` 里。

为什么要做这一步：

- 之前每个接口都要手写 `fetch`、`response.json()`、`result.code === 200`，代码重复很多。
- 后端返回结构已经统一成 `{ code, message, data }`，前端也应该统一处理这个结构。
- 以后新增页面或新增接口时，只需要调用 `apiGet`、`apiPost` 这类方法，不用每次重新写一遍错误处理。

这一步新增或修改了什么：

```text
frontend/src/api.js
frontend/src/App.vue
README.md
```

每个文件的作用：

- `api.js`：前端统一请求工具，专门负责发送请求、解析后端统一返回结构、遇到业务错误时抛出错误。
- `App.vue`：不再直接到处写 `fetch`，改成调用 `apiGet`、`apiPost`、`apiPut`、`apiPatch`、`apiDelete`。
- `README.md`：记录第 21 步的学习目标、文件职责和每个请求方法的含义。

你需要理解：

- `apiRequest(url, options)`：最底层的请求方法，所有 GET、POST、PUT、PATCH、DELETE 最后都会走到这里。
- `fetch(url, options)`：浏览器自带的请求接口，用来真正发送 HTTP 请求。
- `response.json()`：把后端返回的 JSON 字符串解析成 JavaScript 对象。
- `result.code !== 200`：说明后端返回了业务错误，比如账号重复、参数校验失败、用户不存在。
- `throw new ApiError(...)`：主动抛出一个错误，让页面里的 `catch` 统一接住并展示错误信息。
- `return result.data`：页面真正关心的是 `data`，所以封装后调用接口可以直接拿到数据。
- `apiGet(url)`：查询数据时使用，比如列表、详情、统计、下拉选项。
- `apiPost(url, body)`：新增数据时使用，比如新增用户。
- `apiPut(url, body)`：整体修改数据时使用，比如编辑用户。
- `apiPatch(url, body)`：局部修改数据时使用，比如只修改用户状态。
- `apiDelete(url)`：删除数据时使用，比如删除用户。
- `getApiErrorMessage(error, networkMessage)`：页面里的错误转换函数，业务错误显示后端 message，网络错误显示“后端未启动”这类提示。

改造前：

```javascript
const response = await fetch('/api/users/stats')
const result = await response.json()

if (result.code === 200) {
  userStats.value = result.data
} else {
  statsError.value = result.message || '用户统计接口返回异常'
}
```

改造后：

```javascript
userStats.value = await apiGet('/api/users/stats')
```

这一步没有新增后端接口，只是把前端请求代码整理得更像真实项目。

### 第 22 步：登录接口和登录表单

目标：新增后台登录接口 `POST /api/auth/login`，并在前端页面增加一个登录表单，理解登录请求从前端到后端的完整流程。

接口：

```text
POST /api/auth/login
```

请求体示例：

```json
{
  "username": "admin",
  "password": "123456"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "role": "超级管理员"
  }
}
```

为什么要做这一步：

- 后台管理系统通常需要先登录，再进入管理页面。
- 登录接口可以继续练习 `DTO -> Controller -> Service -> Repository -> VO` 的后端分层。
- 这一步先做学习版登录，重点理解流程；真实项目后面还要继续升级密码加密、token 和权限拦截。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/dto/LoginDTO.java
backend/src/main/java/com/example/admin/auth/vo/LoginVO.java
backend/src/main/java/com/example/admin/auth/service/AuthService.java
backend/src/main/java/com/example/admin/auth/controller/AuthController.java
backend/src/main/java/com/example/admin/user/entity/UserEntity.java
backend/src/main/java/com/example/admin/user/repository/UserRepository.java
backend/src/main/java/com/example/admin/user/config/UserSchemaInitializer.java
backend/src/main/java/com/example/admin/user/config/UserDataInitializer.java
backend/src/main/java/com/example/admin/user/service/UserService.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/App.vue
frontend/src/style.css
README.md
```

每个文件的作用：

- `LoginDTO.java`：接收前端传来的 `username` 和 `password`，并校验不能为空。
- `LoginVO.java`：登录成功后返回给前端的用户信息，不返回密码。
- `AuthService.java`：处理登录业务，查账号、比对密码、判断账号是否禁用。
- `AuthController.java`：提供 `POST /api/auth/login` 接口入口。
- `UserEntity.java`：新增 `password` 字段，对应数据库里的密码列。
- `UserRepository.java`：新增 `findByUsername`，登录时用账号查询用户。
- `UserSchemaInitializer.java`：旧 H2 数据库启动时自动补 `password` 列，并给旧用户填默认密码。
- `UserDataInitializer.java`：新数据库初始化演示用户时设置默认密码 `123456`。
- `UserService.java`：新增用户时也设置默认密码 `123456`，避免密码字段为空。
- `AuthControllerTest.java`：测试登录成功、密码错误、禁用用户不能登录。
- `App.vue`：新增登录表单、当前登录用户状态、登录和退出方法。
- `style.css`：新增登录表单样式。

你需要理解：

- `LoginDTO`：登录请求参数对象，属于“前端传给后端”的数据。
- `LoginVO`：登录响应对象，属于“后端返回给前端”的数据。
- `findByUsername`：Spring Data JPA 根据方法名自动生成按账号查询的 SQL。
- `BusinessException`：登录失败时抛业务异常，比如账号密码错误、账号被禁用。
- `@RequestMapping("/api/auth")`：认证相关接口统一放到 `/api/auth` 前缀下。
- `@PostMapping("/login")`：表示这个方法处理 `POST /api/auth/login`。
- `currentUser`：前端保存当前登录用户信息。
- `loginForm`：前端登录表单数据，提交给登录接口。

注意：这一步为了学习流程，密码暂时是明文 `123456`。真实项目不能明文保存密码，后面会继续学习密码加密和 token 登录。

### 第 23 步：保存登录状态和学习版 token

目标：登录成功后让后端返回一个学习版 `token`，前端把登录用户和 token 保存到 `localStorage`，刷新页面后仍然能恢复当前登录用户。

为什么要做这一步：

- 第 22 步登录成功后，用户信息只存在内存变量 `currentUser` 里，刷新页面就会丢失。
- 后台管理系统通常需要一个登录凭证，后续请求可以带着这个凭证告诉后端“我已经登录”。
- 这一步先学习 token 和本地保存登录态的基本形状，下一步再继续学习接口拦截和权限校验。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/vo/LoginVO.java
backend/src/main/java/com/example/admin/auth/service/AuthService.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/App.vue
frontend/src/api.js
README.md
```

每个文件的作用：

- `LoginVO.java`：新增 `token` 字段，登录成功后返回给前端。
- `AuthService.java`：新增 `createLearningToken`，生成 `study-token-用户ID-随机字符串`。
- `AuthControllerTest.java`：补充断言，确认登录成功会返回 `study-token-` 开头的 token。
- `App.vue`：登录成功后保存用户信息，页面打开时从 `localStorage` 恢复登录用户，退出时清除登录信息。
- `api.js`：请求时自动从 `localStorage` 读取 token，并放到 `Authorization` 请求头里。
- `README.md`：记录第 23 步的学习目标和代码含义。

你需要理解：

- `token`：登录成功后后端返回给前端的一段凭证字符串。
- `UUID.randomUUID()`：Java 生成随机字符串的工具，这里用来让每次登录的 token 不一样。
- `localStorage`：浏览器本地存储，刷新页面后数据还在。
- `JSON.stringify(user)`：把 JavaScript 对象转成字符串，才能保存到 `localStorage`。
- `JSON.parse(savedUser)`：把 `localStorage` 里保存的字符串恢复成对象。
- `Authorization`：常见的请求头，用来携带登录凭证。
- `Bearer ${token}`：token 请求头的常见格式，意思是“这是一个 bearer token”。

注意：这一步的 token 仍然是学习版。后端目前只是返回 token，前端也会携带 token，但还没有真正校验 token。真实项目通常会使用 JWT 或服务端 session，并且每个需要登录的接口都要校验权限。

### 第 24 步：后端登录拦截器

目标：让后端真正开始检查 `Authorization` 请求头。访问 `/api/users` 和 `/api/users/**` 时，必须带上学习版 token；没有 token 时返回 `401 请先登录`。

为什么要做这一步：

- 第 23 步前端已经会保存 token，也会自动把 token 放到请求头里。
- 但是后端如果不检查 token，这个 token 就只是“前端自己带着玩”，没有保护接口的作用。
- 拦截器可以在请求进入 Controller 之前先做统一检查，避免每个 Controller 方法都重复写登录校验。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/interceptor/LoginInterceptor.java
backend/src/main/java/com/example/admin/config/WebConfig.java
backend/src/test/java/com/example/admin/user/controller/UserControllerTest.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `LoginInterceptor.java`：新增登录拦截器，在 Controller 执行前检查 `Authorization` 请求头。
- `WebConfig.java`：把登录拦截器注册到 Spring MVC，并指定拦截 `/api/users` 和 `/api/users/**`。
- `UserControllerTest.java`：用户接口测试统一带上测试 token，并新增“没 token 返回 401”的测试。
- `App.vue`：未登录时不主动请求用户接口；登录成功后再刷新用户列表和统计；退出登录后清空受保护数据。
- `README.md`：记录第 24 步的学习目标和代码含义。

你需要理解：

- `HandlerInterceptor`：Spring MVC 的拦截器接口，请求进入 Controller 前会先经过它。
- `preHandle`：拦截器里最常用的方法，返回 `true` 表示放行，返回 `false` 表示拦住。
- `request.getHeader("Authorization")`：从 HTTP 请求头里读取 token。
- `startsWith("Bearer study-token-")`：学习版校验，只判断 token 格式是否正确。
- `response.setStatus(401)`：告诉前端“当前请求没有登录权限”。
- `ObjectMapper`：把 `ApiResponse.error(401, "请先登录")` 这种 Java 对象转换成 JSON 字符串。
- `WebMvcConfigurer`：Spring MVC 配置扩展点，常用来注册拦截器、跨域配置等。
- `addPathPatterns("/api/users", "/api/users/**")`：指定哪些接口需要经过登录拦截器。

注意：这一步仍然是学习版校验。现在只检查 token 字符串格式，没有校验 token 是否真的是后端签发的。后面学习 JWT 时，会继续升级成“后端签发、后端验签、解析当前登录用户”的真实流程。

### 第 25 步：使用 BCrypt 加密密码

目标：把数据库里的明文密码升级成 BCrypt 密文。登录时不再用 `equals` 比较明文，而是用 `passwordEncoder.matches` 校验“用户输入的明文密码”和“数据库保存的密文”是否匹配。

为什么要做这一步：

- 第 22 步为了学习流程，数据库里暂时保存了明文密码 `123456`。
- 真实项目绝对不能保存明文密码，因为数据库一旦泄露，用户密码会直接暴露。
- BCrypt 会给密码加盐并多轮计算，即使两个用户密码一样，生成的密文通常也不一样。

这一步新增或修改了什么：

```text
backend/pom.xml
backend/src/main/java/com/example/admin/auth/config/PasswordConfig.java
backend/src/main/java/com/example/admin/auth/service/AuthService.java
backend/src/main/java/com/example/admin/user/config/UserDataInitializer.java
backend/src/main/java/com/example/admin/user/service/UserService.java
README.md
```

每个文件的作用：

- `pom.xml`：新增 `spring-security-crypto` 依赖，提供 BCrypt 密码工具。
- `PasswordConfig.java`：新增 `BCryptPasswordEncoder` Bean，让其他类可以通过构造方法注入使用。
- `AuthService.java`：登录时使用 `passwordEncoder.matches(明文密码, 数据库密文)` 校验密码。
- `UserDataInitializer.java`：初始化演示用户时保存 BCrypt 密文，并把旧数据库里的明文 `123456` 自动升级成密文。
- `UserService.java`：后台新增用户时，默认密码 `123456` 也保存成 BCrypt 密文。
- `README.md`：记录第 25 步的学习目标和代码含义。

你需要理解：

- `spring-security-crypto`：Spring 提供的密码加密工具包，可以单独使用，不等于启用完整 Spring Security。
- `BCryptPasswordEncoder`：BCrypt 密码工具类。
- `passwordEncoder.encode("123456")`：把明文密码生成 BCrypt 密文，保存到数据库。
- `passwordEncoder.matches(rawPassword, encodedPassword)`：校验明文密码和密文是否匹配。
- BCrypt 密文不能“解密回明文”：登录时不是解密，而是用算法判断输入密码是否能匹配这个密文。
- `@Bean`：把一个对象交给 Spring 容器管理，其他类可以直接注入它。
- 旧数据迁移：已有 H2 数据库里可能还保存明文 `123456`，所以启动时需要自动转成 BCrypt 密文。

注意：这一步只解决“密码不能明文保存”。当前 token 仍然是学习版，后面还会继续学习真实 JWT、权限角色和接口授权。

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
