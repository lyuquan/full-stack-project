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

### 第 26 步：服务端校验 token 是否真的登录过

目标：把 token 从“只看格式”升级成“后端真的登记过才有效”。登录成功时，后端生成 token 并保存到内存 token 仓库；访问用户接口时，拦截器会检查这个 token 是否存在。

为什么要做这一步：

- 第 24 步的拦截器只判断 `Authorization` 是否以 `Bearer study-token-` 开头。
- 这样别人随便伪造一个 `Bearer study-token-fake` 也能通过，安全性很弱。
- 这一步让后端保存自己签发过的 token，请求带来的 token 必须能在服务端查到。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/service/AuthTokenService.java
backend/src/main/java/com/example/admin/auth/service/AuthService.java
backend/src/main/java/com/example/admin/auth/interceptor/LoginInterceptor.java
backend/src/test/java/com/example/admin/user/controller/UserControllerTest.java
README.md
```

每个文件的作用：

- `AuthTokenService.java`：新增学习版 token 服务，用内存 `ConcurrentHashMap` 保存 token 和登录用户信息。
- `AuthService.java`：登录成功时调用 `authTokenService.createToken(user)`，由 token 服务生成并登记 token。
- `LoginInterceptor.java`：从 `Authorization` 请求头取出 token，并调用 `authTokenService.isValid(token)` 校验是否真的存在。
- `UserControllerTest.java`：用户接口测试先登录拿真实 token；新增“伪造 token 不能访问”的测试。
- `README.md`：记录第 26 步的学习目标和代码含义。

你需要理解：

- `ConcurrentHashMap`：线程安全的 Map，适合 Web 项目里多个请求同时读写。
- `tokenStore`：服务端内存里的 token 仓库，key 是 token，value 是登录用户信息。
- `createToken(user)`：登录成功后生成 token，并把 token 登记到服务端。
- `isValid(token)`：判断 token 是否能在服务端查到。
- `Authorization: Bearer xxx`：前端请求头的完整格式，后端会取出 `xxx` 这部分做校验。
- 伪造 token：格式看起来对，但没有在服务端登记过，所以现在会返回 `401 请先登录`。

注意：这仍然不是最终生产方案。因为 token 存在内存里，后端重启后 token 会全部丢失，多台服务器之间也不能共享。真实项目常见方案是 JWT、Redis token/session，或者完整的 Spring Security 认证授权体系。

### 第 27 步：当前用户接口和退出登录接口

目标：补全登录闭环。新增 `GET /api/auth/me` 查询当前登录用户，新增 `POST /api/auth/logout` 让后端删除当前 token，退出后旧 token 不能再访问受保护接口。

为什么要做这一步：

- 登录成功只是拿到 token，不代表登录流程完整。
- 后台系统通常需要一个“当前登录用户”接口，用来刷新页面后确认 token 仍然有效。
- 退出登录不能只清理前端 `localStorage`，后端也应该让这个 token 失效。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/service/AuthTokenService.java
backend/src/main/java/com/example/admin/auth/controller/AuthController.java
backend/src/main/java/com/example/admin/auth/interceptor/LoginInterceptor.java
backend/src/main/java/com/example/admin/config/WebConfig.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/api.js
frontend/src/App.vue
README.md
```

每个文件的作用：

- `AuthTokenService.java`：新增从请求头读取 token、根据请求查询登录用户、删除当前 token 的方法。
- `AuthController.java`：新增 `GET /api/auth/me` 和 `POST /api/auth/logout`。
- `LoginInterceptor.java`：复用 `AuthTokenService.getToken(request)`，避免重复解析 `Bearer token`。
- `WebConfig.java`：把 `/api/auth/me` 和 `/api/auth/logout` 加入登录拦截器保护范围。
- `AuthControllerTest.java`：新增当前用户接口测试和退出登录后 token 失效测试。
- `api.js`：新增 `clearStoredLoginUser()`，统一清理前端登录缓存。
- `App.vue`：退出登录时先请求后端 `/api/auth/logout`，再清理前端登录状态和用户数据。
- `README.md`：记录第 27 步的学习目标和代码含义。

你需要理解：

- `GET /api/auth/me`：查询当前 token 对应的登录用户。
- `POST /api/auth/logout`：删除当前请求里的 token，让它失效。
- `authTokenService.removeToken(request)`：从请求头取出 token，并从服务端 token 仓库删除。
- `clearStoredLoginUser()`：前端清理 `localStorage` 的统一方法。
- 退出登录要前后端都做：后端让 token 失效，前端清理本地保存的用户信息。

注意：当前 `/me` 和 `/logout` 已经被登录拦截器保护。没有 token 或 token 已失效时，请求会直接返回 `401 请先登录`。

### 第 28 步：刷新页面时校验登录状态

目标：前端刷新页面时，不再只相信 `localStorage` 里的用户信息，而是先调用 `GET /api/auth/me` 让后端确认 token 是否仍然有效。

为什么要做这一步：

- 第 27 步已经有了 `/api/auth/me`，但前端刷新时还只是从 `localStorage` 直接恢复用户。
- 现在 token 存在后端内存里，后端重启后 token 会丢失；浏览器里的旧 token 还在，但后端已经不认识它。
- 所以刷新页面时要问后端：“这个 token 还有效吗？”有效才恢复登录，无效就清理本地登录状态。

这一步新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/service/AuthTokenService.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/api.js
frontend/src/App.vue
README.md
```

每个文件的作用：

- `AuthTokenService.java`：让 token 仓库里的登录用户信息增加 `nickname`，方便 `/api/auth/me` 返回完整展示信息。
- `AuthControllerTest.java`：补充断言，确认登录接口和 `/api/auth/me` 都会返回 `nickname`。
- `api.js`：新增并统一 `saveStoredLoginUser`、`getStoredLoginUser`、`clearStoredLoginUser`，集中管理浏览器登录缓存。
- `App.vue`：页面打开时先读取本地 token，再调用 `/api/auth/me` 校验；token 有效才恢复登录并加载用户数据，token 无效就清理本地缓存。
- `README.md`：记录第 28 步的学习目标和代码含义。

你需要理解：

- `localStorage` 只能说明浏览器曾经保存过 token，不能证明 token 现在还有效。
- `/api/auth/me` 是“校验当前登录状态”的接口，前端刷新页面时很常用。
- `await restoreLoginUser()`：页面初始化时先等待登录状态恢复完成，再决定是否请求用户管理接口。
- `saveStoredLoginUser(currentUser.value)`：把后端确认后的用户信息重新保存一次，保证本地缓存是最新的。
- 未登录时不请求 `/api/users/**`：因为这些接口已经被登录拦截器保护，没 token 请求只会得到 401。

注意：这一步依然是学习版内存 token。真实项目里，如果用 JWT，刷新时也常常会用 `/me` 或类似接口确认当前用户信息和权限。

### 第 29 步：管理员权限控制

目标：在“已经登录”的基础上继续判断“有没有权限”。这一步规定：用户查询接口登录后都能看，但新增、编辑、启用禁用、删除用户这些写操作，只有超级管理员可以执行。

为什么要做这一步：

- 登录只能证明“你是谁”，不能证明“你能做什么”。
- 后台管理系统通常会有不同角色，比如超级管理员、运营管理员、只读用户。
- 前端隐藏按钮只是体验优化，真正的权限判断必须放在后端。
- 如果普通用户直接用接口工具请求 `DELETE /api/users/1`，后端也应该能拦住。

这一新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/interceptor/AdminPermissionInterceptor.java
backend/src/main/java/com/example/admin/config/WebConfig.java
backend/src/test/java/com/example/admin/user/controller/UserControllerTest.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `AdminPermissionInterceptor.java`：新增管理员权限拦截器。它会先判断请求方法，`GET` 查询请求直接放行；`POST`、`PUT`、`PATCH`、`DELETE` 写请求必须是超级管理员才放行，否则返回 `403 没有操作权限`。
- `WebConfig.java`：把管理员权限拦截器注册到 `/api/users` 和 `/api/users/**`。这样用户管理接口会先经过登录拦截器，再经过权限拦截器。
- `UserControllerTest.java`：新增权限测试。超级管理员删除不存在的用户会进入 Controller 并返回业务 `404`；运营管理员会在权限拦截器处直接返回 HTTP `403`。
- `App.vue`：新增 `canManageUsers` 计算属性，用当前登录用户角色判断是否可以操作用户数据；普通角色登录后，新增、编辑、启用禁用、删除按钮会被禁用。
- `README.md`：记录第 29 步的学习目标、改动文件和代码含义。

你需要理解：

- `401 Unauthorized`：没有登录，或者 token 无效。
- `403 Forbidden`：已经登录了，但当前角色没有权限执行这个操作。
- `HandlerInterceptor`：Spring MVC 的拦截器，可以在请求进入 Controller 前统一做登录、权限、日志等处理。
- `request.getMethod()`：读取 HTTP 请求方法，比如 `GET`、`POST`、`PUT`、`PATCH`、`DELETE`。
- `UserConstants.ROLE_SUPER_ADMIN.equals(loginUser.getRole())`：判断当前登录用户是不是超级管理员。
- 权限判断要放在后端：前端可以禁用按钮，但不能作为安全边界。

注意：这一步还是学习版权限控制。真实项目里通常会用 Spring Security、权限注解、菜单权限、按钮权限、接口权限表等更完整的方案。我们现在先用拦截器把核心思想跑通。

### 第 30 步：登录用户返回权限能力

目标：让登录接口和当前用户接口直接返回 `canManageUsers`，前端根据这个布尔值控制用户管理按钮，而不是自己猜哪个角色能操作。

为什么要做这一步：

- 第 29 步前端通过角色判断是否能操作用户，但角色文字只是身份，不应该让前端自己推导权限。
- 后端才是权限规则的来源，所以应该由后端告诉前端“当前用户能不能管理用户”。
- 前端拿到 `canManageUsers: true/false` 后，按钮显示逻辑会更简单，也更接近真实项目。

这一新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/vo/LoginVO.java
backend/src/main/java/com/example/admin/auth/service/AuthService.java
backend/src/main/java/com/example/admin/auth/service/AuthTokenService.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `LoginVO.java`：登录成功返回给前端的数据里新增 `canManageUsers` 字段，表示当前用户是否可以管理用户数据。
- `AuthService.java`：登录时根据用户角色计算 `canManageUsers`。现在超级管理员返回 `true`，其他角色返回 `false`。
- `AuthTokenService.java`：把 `canManageUsers` 也保存到内存 token 仓库里，所以 `/api/auth/me` 刷新登录状态时也能返回权限能力。
- `AuthControllerTest.java`：新增断言，确认登录接口和 `/api/auth/me` 都返回 `canManageUsers: true`。
- `App.vue`：前端的 `canManageUsers` 计算属性改为直接读取 `currentUser.canManageUsers`，不再依赖角色下拉选项。
- `README.md`：记录第 30 步的学习目标和代码含义。

你需要理解：

- `role`：用户角色，比如超级管理员、运营管理员、只读用户。
- `canManageUsers`：权限能力，表示“能不能管理用户”。
- 角色不等于权限：一个角色可以拥有多个权限，一个权限也可以分配给多个角色。
- 前端适合根据权限字段控制按钮显示；后端必须继续用拦截器做真正的权限校验。
- 布尔值字段适合命名成 `canXxx`，比如 `canManageUsers`、`canDeleteOrder`、`canViewReport`。

注意：这一步只是把一个权限能力写死在代码里。真实项目里，权限通常来自数据库权限表、角色权限关联表，或者 Spring Security 的权限体系。

### 第 31 步：返回权限码列表

目标：让登录接口和当前用户接口返回 `permissions` 权限码列表。前端根据权限码判断按钮是否可用，后端拦截器也根据权限码判断写操作是否放行。

为什么要做这一步：

- 第 30 步只有一个 `canManageUsers`，适合单个功能，但权限多了以后会变成很多布尔字段。
- 真实后台通常使用权限码，比如 `user:read`、`user:write`、`role:write`、`report:view`。
- 权限码列表更通用，前端可以用同一个 `hasPermission(code)` 方法判断不同按钮。
- 后端也可以统一检查权限码，不需要在每个地方都写“某某角色可以做某事”。

这一新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/constant/AuthPermissions.java
backend/src/main/java/com/example/admin/auth/vo/LoginVO.java
backend/src/main/java/com/example/admin/auth/service/AuthService.java
backend/src/main/java/com/example/admin/auth/service/AuthTokenService.java
backend/src/main/java/com/example/admin/auth/interceptor/AdminPermissionInterceptor.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `AuthPermissions.java`：新增权限码常量类，集中定义 `user:read` 和 `user:write`，并提供按角色生成权限列表的方法。
- `LoginVO.java`：登录返回数据新增 `permissions` 字段，前端可以拿到当前用户拥有的权限码列表。
- `AuthService.java`：登录时调用 `AuthPermissions.listByRole(role)` 生成权限列表，再根据权限列表计算 `canManageUsers`。
- `AuthTokenService.java`：把 `permissions` 保存进 token 仓库，所以刷新页面调用 `/api/auth/me` 时也能拿到权限列表。
- `AdminPermissionInterceptor.java`：写操作不再直接判断角色，而是判断当前用户是否拥有 `user:write` 权限。
- `AuthControllerTest.java`：新增测试，确认超级管理员拥有 `user:read` 和 `user:write`，运营管理员只有 `user:read`。
- `App.vue`：新增 `hasPermission(permissionCode)`，前端按钮权限改为从 `permissions` 列表判断。
- `README.md`：记录第 31 步的学习目标和代码含义。

你需要理解：

- `permissions`：权限码数组，例如 `["user:read", "user:write"]`。
- `user:read`：读取用户数据的权限。
- `user:write`：新增、编辑、启用禁用、删除用户的权限。
- `hasPermission('user:write')`：前端检查当前登录用户是否拥有某个权限码。
- `canManageUsers`：保留的快捷字段，方便页面直接使用；它现在由权限列表计算出来。
- 权限码比角色更灵活：以后如果“运营管理员”也允许编辑用户，只需要给它分配 `user:write`，不用改前端按钮逻辑。

注意：这一步的权限列表仍然是代码写死的。真实项目里，权限码通常会存到数据库，通过角色权限关联表查询出来。

### 第 32 步：前端动态菜单权限

目标：根据当前登录用户的 `permissions` 权限码列表动态显示左侧菜单。

为什么要做这一步：

- 第 31 步已经让后端返回权限码列表，但左侧菜单还是前端写死的。
- 后台系统常见做法是：用户有什么页面权限，就显示什么菜单。
- 菜单权限属于前端体验；接口权限仍然必须由后端拦截器兜底。
- 这样普通用户不会看到自己不能进入的入口，超级管理员能看到更多管理入口。

这一新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/constant/AuthPermissions.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/App.vue
frontend/src/style.css
README.md
```

每个文件的作用：

- `AuthPermissions.java`：新增 `role:manage` 权限码，用来控制“角色管理”菜单是否显示；超级管理员拥有这个权限，普通角色暂时没有。
- `AuthControllerTest.java`：更新测试，确认超级管理员登录后会返回 `user:read`、`user:write`、`role:manage` 三个权限码。
- `App.vue`：新增 `allMenus` 菜单配置、`availableMenus` 计算属性和 `ROLE_MANAGE_PERMISSION` 常量；左侧菜单从写死改成按权限过滤后渲染。
- `style.css`：给菜单项补充稳定高度和行高，避免动态菜单变化时样式跳动。
- `README.md`：记录第 32 步的学习目标和代码含义。

你需要理解：

- `allMenus`：前端完整菜单配置，表示系统理论上有哪些菜单。
- `availableMenus`：当前登录用户真正能看到的菜单。
- `permission`：菜单需要的权限码；没有权限码的菜单表示所有人都能看到。
- `v-for="menu in availableMenus"`：Vue 根据数组循环渲染菜单。
- `:class="{ active: menu.active }"`：根据菜单数据动态决定是否加 active 样式。
- 菜单权限不等于接口权限：隐藏菜单不能防止别人直接调接口，所以后端拦截器仍然必须保留。

注意：这一步只是前端动态显示菜单。真实项目里，菜单配置也可能来自后端接口，比如 `/api/auth/menus`，后面可以继续学习。

### 第 33 步：后端返回菜单列表

目标：新增 `GET /api/auth/menus`，由后端根据当前登录用户的权限返回左侧菜单，前端只负责渲染接口返回的数据。

为什么要做这一步：

- 第 32 步菜单虽然已经按权限动态显示，但完整菜单配置仍然写在前端。
- 真实后台项目里，菜单经常来自后端，这样后端可以统一控制角色、权限、菜单关系。
- 前端不需要知道“哪个权限对应哪个菜单”，只需要渲染后端返回的 `key`、`label`、`active`。
- 后面如果菜单变多，前端不用频繁改权限判断规则。

这一新增或修改了什么：

```text
backend/src/main/java/com/example/admin/auth/vo/MenuVO.java
backend/src/main/java/com/example/admin/auth/service/AuthService.java
backend/src/main/java/com/example/admin/auth/controller/AuthController.java
backend/src/main/java/com/example/admin/config/WebConfig.java
backend/src/test/java/com/example/admin/auth/controller/AuthControllerTest.java
frontend/src/App.vue
README.md
```

每个文件的作用：

- `MenuVO.java`：新增菜单返回对象，包含 `key`、`label`、`active` 三个字段。
- `AuthService.java`：新增 `listMenus(permissions)`，根据权限列表生成当前用户可见菜单。
- `AuthController.java`：新增 `GET /api/auth/menus`，返回当前登录用户可见菜单。
- `WebConfig.java`：把 `/api/auth/menus` 加入登录拦截器保护范围，没有 token 不能查询菜单。
- `AuthControllerTest.java`：新增菜单接口测试。超级管理员返回首页、用户管理、角色管理；运营管理员不返回角色管理。
- `App.vue`：移除前端写死的完整菜单配置，新增 `menus` 状态和 `loadMenus()`，登录或刷新恢复登录后请求后端菜单。
- `README.md`：记录第 33 步的学习目标和代码含义。

你需要理解：

- `MenuVO`：菜单 VO，控制前端能看到哪些菜单字段。
- `GET /api/auth/menus`：当前登录用户菜单接口。
- `menus.value = await apiGet('/api/auth/menus')`：前端把后端返回的菜单保存起来。
- `v-for="menu in menus"`：前端按后端返回的菜单数组循环渲染。
- 菜单由后端返回，不代表接口不用校验；接口权限仍然由后端拦截器负责。
- 这一步让前端更轻：前端少写权限规则，后端统一管理菜单可见性。

注意：现在菜单仍然是后端代码写死生成的。真实项目里，菜单通常会放进数据库表，例如 `sys_menu`，再通过角色权限关系查询出来。

### 第 34 步：点击菜单切换前端页面

目标：让后端返回的菜单多一个 `path` 字段，前端点击左侧菜单后切换当前页面内容，并根据当前菜单高亮。

为什么要做这一步：

- 第 33 步已经让菜单列表来自后端，但菜单还只是显示文字，不能点击切换页面。
- 真实后台系统里，菜单通常会包含 `key`、`label`、`path`、`icon`、`permission` 等信息。
- 后端负责告诉前端“当前用户能看到哪些菜单”，前端负责维护“当前选中了哪个菜单”。
- 这一步先不用 Vue Router，只用 `currentMenuKey` 学习前端页面状态切换，后面再接真正路由。

接口：

```text
GET /api/auth/menus
```

超级管理员示例返回：

```json
[
  {
    "key": "home",
    "label": "系统首页",
    "path": "/",
    "active": true
  },
  {
    "key": "users",
    "label": "用户管理",
    "path": "/users",
    "active": false
  },
  {
    "key": "roles",
    "label": "角色管理",
    "path": "/roles",
    "active": false
  }
]
```

这一步新增或修改了什么：

- `MenuVO.java`：新增 `path` 字段，表示这个菜单对应的前端页面路径。
- `AuthService.java`：创建菜单时补上 `/`、`/users`、`/roles` 这几个路径。
- `AuthControllerTest.java`：菜单接口测试新增 `path` 断言，保证后端真的返回路径。
- `App.vue`：新增 `currentMenuKey`、`currentMenu`、`isHomePage`、`isUsersPage`、`isRolesPage` 和 `selectMenu(menu)`。
- `style.css`：左侧菜单项从普通文字改成按钮样式，支持点击和 hover 效果。
- `README.md`：记录第 34 步的学习目标、接口结构和文件职责。

你需要理解：

- `path`：菜单对应的页面地址，例如 `/users`。现在先展示在标题上，后面可以接 Vue Router。
- `currentMenuKey`：前端当前选中的菜单 key，例如 `home` 或 `users`。
- `selectMenu(menu)`：点击菜单时执行，把当前菜单 key 改成被点击的菜单。
- `computed`：根据已有状态计算新状态，例如 `isUsersPage` 根据 `currentMenuKey` 判断是否显示用户管理内容。
- `v-if="isUsersPage"`：只有当前菜单是用户管理时，才渲染用户统计、筛选、表单和表格。
- 后端菜单数据和前端页面状态是两件事：后端决定“能看什么”，前端决定“当前正在看什么”。

注意：这一步还没有引入 Vue Router，所以刷新浏览器地址不会进入 `/users` 或 `/roles`。等菜单和页面状态理解清楚后，再学习真正的前端路由会更顺。

### 第 35 步：引入 Vue Router 前端路由

目标：安装并使用 Vue Router，让点击左侧菜单时浏览器地址栏同步变化，例如从 `/` 变成 `/users` 或 `/roles`。

为什么要做这一步：

- 第 34 步已经能切换页面内容，但浏览器地址栏不会变化。
- 真实后台系统通常会用前端路由管理页面地址，例如用户管理页是 `/users`，角色管理页是 `/roles`。
- 菜单接口返回的 `path` 字段应该真正参与页面跳转，而不是只显示在标题里。
- 有了路由后，用户可以刷新页面、复制地址、手动输入地址，前端也能知道当前应该显示哪个页面。

这一步新增或修改了什么：

- `package.json`：新增 `vue-router` 依赖，这是 Vue 官方路由库。
- `package-lock.json`：记录本次安装后精确的依赖版本，保证别人 `npm install` 时版本一致。
- `main.js`：创建 `router`，注册 `/`、`/users`、`/roles` 三个前端路径，并通过 `.use(router)` 安装到 Vue 应用。
- `App.vue`：使用 `useRoute()` 读取当前地址，使用 `useRouter()` 在点击菜单时跳转地址。
- `README.md`：记录第 35 步的学习目标、文件职责和核心概念。

你需要理解：

- `vue-router`：Vue 官方前端路由库，用来管理浏览器地址和页面展示之间的关系。
- `createRouter(...)`：创建路由对象。
- `createWebHistory()`：使用正常 URL 模式，例如 `/users`，而不是 `/#/users`。
- `routes`：前端路由表，告诉 Vue Router 当前项目有哪些页面地址。
- `router.push(menu.path)`：点击菜单时跳转到菜单对应路径。
- `route.path`：当前浏览器地址里的路径，例如 `/users`。
- `watch(() => route.path, ...)`：监听地址变化，地址变了就重新计算当前菜单。
- `normalizeCurrentMenu()`：防止用户访问自己看不到的菜单路径，例如普通用户手动输入 `/roles` 时，会回到第一个可见菜单。

注意：这一步为了降低学习难度，还没有把页面拆成多个 Vue 文件，也没有使用 `<router-view>`。现在先让路由和菜单联动跑通，下一步可以继续把“用户管理页”和“角色管理页”拆成独立页面组件。

### 第 36 步：角色列表接口

目标：新增 `GET /api/roles`，让“角色管理”页面可以从后端查询角色列表。

为什么要做这一步：

- 第 35 步已经有 `/roles` 前端路由，但角色管理页还只是占位内容。
- 后台管理系统通常不只有用户模块，还会有角色、权限、菜单等模块。
- 新增一个 `role` 模块，可以继续练习后端常见分层：`Controller -> Service -> VO`。
- 这一步先用固定角色数据，后面再升级成数据库角色表。

接口：

```text
GET /api/roles
```

这一步新增或修改了什么：

- `RoleVO.java`：新增角色列表返回对象，定义前端能看到的角色字段。
- `RoleService.java`：新增角色业务层，先返回固定的三个角色。
- `RoleController.java`：新增角色接口入口，提供 `GET /api/roles`。
- `RoleControllerTest.java`：新增角色接口测试，确认登录后能查角色、未登录不能查角色。
- `WebConfig.java`：把 `/api/roles` 和 `/api/roles/**` 加入登录拦截器保护范围。
- `App.vue`：角色管理页新增 `roles`、`roleLoading`、`roleError` 和 `loadRoles()`，并用表格展示角色列表。
- `README.md`：记录第 36 步的学习目标、接口和文件职责。

你需要理解：

- `RoleController`：接收前端 HTTP 请求，例如 `GET /api/roles`。
- `RoleService`：处理角色业务逻辑。现在是固定数组，后面可以改成查数据库。
- `RoleVO`：返回给前端展示的数据结构，不等于数据库表。
- `List<RoleVO>`：表示返回多个角色对象。
- `@RestController`：告诉 Spring 这个类返回 JSON 接口。
- `@RequestMapping("/api/roles")`：定义这个 Controller 的统一接口前缀。
- `@GetMapping`：表示这个方法处理 GET 请求。
- 前端 `loadRoles()`：调用 `GET /api/roles`，把返回结果保存到 `roles`，页面再用 `v-for` 渲染表格。

注意：这一步的角色列表还是写死在 Java 代码里的。真实项目中会继续建 `sys_role` 表、`RoleEntity`、`RoleRepository`，再从数据库查询角色列表。

### 第 37 步：拆分前端路由页面组件

目标：把原来挤在 `App.vue` 里的页面内容拆到 `frontend/src/pages` 目录，让 Vue Router 真正渲染不同页面组件。

为什么要做这一步：

- 第 35 步已经引入了 Vue Router，但当时路由组件还是占位写法。
- 后台系统功能会越来越多，如果所有页面都写在 `App.vue`，文件会越来越长，也越来越难维护。
- 真实前端项目通常会把页面放到 `pages` 或 `views` 目录，例如首页、用户管理页、角色管理页。
- `App.vue` 更适合做全局布局：左侧菜单、顶部栏、登录状态、路由出口。

这一步新增或修改了什么：

- `frontend/src/pages/HomePage.vue`：新增首页组件，负责展示后端连接状态。
- `frontend/src/pages/UsersPage.vue`：新增用户管理页面组件，负责展示用户统计、详情、筛选、表单和表格。
- `frontend/src/pages/RolesPage.vue`：新增角色管理页面组件，负责展示角色列表。
- `frontend/src/main.js`：路由表不再使用占位组件，而是分别指向 `HomePage`、`UsersPage`、`RolesPage`。
- `frontend/src/App.vue`：从“所有页面都写在一个文件”改成“全局布局 + `<RouterView>`”，并把页面需要的数据和方法传给路由页面组件。
- `README.md`：记录第 37 步的学习目标、文件职责和核心概念。

你需要理解：

- `pages`：页面级组件目录，通常一个路由对应一个页面组件。
- `<RouterView>`：Vue Router 的路由出口，当前地址匹配哪个路由，就渲染哪个页面组件。
- `props`：父组件传给子组件的数据，例如 `users`、`userStats`、`roles`。
- `emit`：子组件通知父组件做事，例如点击“刷新用户”时触发 `loadUsers`。
- `App.vue`：现在更像后台系统的外壳，负责菜单、登录、刷新和数据协调。
- `UsersPage.vue`：只关心用户管理页面怎么显示，不直接管理登录和菜单。

注意：这一步为了降低学习难度，数据请求方法还放在 `App.vue`。后面可以继续把用户模块的请求和状态也移动到 `UsersPage.vue`，让每个页面自己管理自己的数据。

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

### 第 38 步：角色详情接口

目标：在角色管理页面点击“查看”，调用后端 `GET /api/roles/{code}` 查询单个角色详情。

为什么要做这一步：

- 角色列表 `GET /api/roles` 返回的是一批角色，适合表格展示。
- 角色详情 `GET /api/roles/{code}` 返回的是一个角色，适合详情面板展示。
- 这一步继续练习后端常见写法：`@PathVariable` 从 URL 路径里读取参数。
- 查不到角色时，后端返回统一结构 `{ code: 404, message: "...", data: null }`，前端可以统一显示错误。

这一步新增或修改了什么：

- `RoleService.java`：新增 `getRoleByCode(code)`，根据角色编码查找一个角色。
- `RoleController.java`：新增 `GET /api/roles/{code}`，接收路径里的角色编码并返回详情。
- `RoleControllerTest.java`：新增角色详情测试，覆盖查到角色和查不到角色两种情况。
- `App.vue`：新增角色详情状态、请求方法和清空详情方法。
- `RolesPage.vue`：角色表格新增“查看”按钮，并新增角色详情展示区域。
- `style.css`：给角色详情区域补充间距和分隔线。

你需要理解：

- `@GetMapping("/{code}")`：匹配 `/api/roles/operator` 这种路径。
- `@PathVariable String code`：把 URL 里的 `operator` 取出来放到 Java 变量 `code` 中。
- `getRoleByCode`：Service 层业务方法，负责真正查找角色。
- `selectedRole`：前端保存当前正在查看的角色详情。
- `emit('loadRoleDetail', role.code)`：子组件告诉父组件“我要查这个角色详情”。

### 第 39 步：角色数据接入 H2 数据库

目标：把角色列表从 Java 代码里写死的数组，改成从 H2 数据库的 `sys_role` 表查询。

为什么要做这一步：

- 写死在 Java 代码里的角色，每次改数据都要改代码、重启后端。
- 后台系统里的角色通常属于业务数据，应该存在数据库表里。
- 这一步让角色模块也拥有完整的数据访问分层：`Controller -> Service -> Repository -> Database`。
- 前端接口地址不用变，仍然调用 `GET /api/roles` 和 `GET /api/roles/{code}`，说明后端内部实现可以独立升级。

这一步新增或修改了什么：

- `RoleEntity.java`：新增角色数据库实体，映射 `sys_role` 表。
- `RoleRepository.java`：新增角色数据库访问层，负责查角色表。
- `RoleDataInitializer.java`：新增角色初始化器，数据库为空时插入三条演示角色。
- `RoleService.java`：从“返回固定数组”改成“查询数据库，再把 Entity 转成 VO”。
- `README.md`：记录第 39 步学习内容。

你需要理解：

- `@Entity`：告诉 JPA 这个 Java 类是一张数据库表。
- `@Table(name = "sys_role")`：指定数据库表名是 `sys_role`。
- `JpaRepository<RoleEntity, Long>`：第一个泛型是表对应的实体类，第二个泛型是主键 ID 类型。
- `findByCode(code)`：Spring Data JPA 根据方法名自动生成查询 SQL。
- `RoleDataInitializer`：后端启动后自动准备演示数据，避免你手动去 H2 插入角色。
- `toVO(roleEntity)`：把数据库实体转换成前端展示对象，避免 Controller 直接暴露数据库结构。

### 第 40 步：新增角色接口和前端表单

目标：在角色管理页面新增一个表单，提交后调用 `POST /api/roles`，把新角色保存到 H2 数据库。

为什么要做这一步：

- 第 39 步已经让角色数据进入数据库，这一步开始真正写入数据库。
- 新增数据通常使用 `POST` 请求，前端把表单数据作为 JSON 放到请求体里。
- 后端不能只相信前端表单，仍然要用 DTO 注解做参数校验。
- 角色编码 `code` 是接口路径和程序逻辑会使用的稳定值，所以必须限制格式并且不能重复。

这一步新增或修改了什么：

- `CreateRoleDTO.java`：新增角色创建请求对象，接收 `code`、`name`、`description`、`permissionCount`。
- `RoleController.java`：新增 `POST /api/roles`，接收前端提交的 JSON。
- `RoleService.java`：新增 `createRole`，校验角色编码是否重复，并保存到数据库。
- `RoleControllerTest.java`：新增创建成功、编码重复、编码格式非法三个测试。
- `App.vue`：新增 `roleForm`、`saveRole`、`resetRoleForm`，负责调接口和刷新列表。
- `RolesPage.vue`：新增角色表单，点击“新增角色”后触发父组件保存。
- `style.css`：新增通用表单网格样式，角色表单和后续表单都可以复用。
- `README.md`：记录第 40 步学习内容。

你需要理解：

- `POST /api/roles`：新增角色接口。
- `@RequestBody`：从请求体 JSON 里读取数据。
- `@Valid`：触发 DTO 上的校验注解。
- `@Pattern`：限制角色编码格式，例如只允许 `report_admin` 这种小写下划线写法。
- `existsByCode(code)`：保存前查数据库，判断角色编码是否已经存在。
- `roleRepository.save(role)`：把角色实体保存进数据库。
- `v-model="roleForm.code"`：前端输入框和表单对象双向绑定。
- `emit('saveRole')`：页面组件通知 `App.vue` 去调用保存接口。

### 第 41 步：编辑角色接口和前端编辑模式

目标：在角色管理页面点击“编辑”，把角色数据回填到表单，提交后调用 `PUT /api/roles/{code}` 更新数据库。

为什么要做这一步：

- 后台系统里的数据通常都需要编辑能力，角色也一样。
- `POST` 用于新增，`PUT` 通常用于更新一条已有数据。
- 角色的 `code` 已经作为 URL 标识使用，例如 `/api/roles/operator`，所以这一步不允许编辑 `code`，只编辑名称、说明和权限数量。
- 这样可以继续练习 `Controller -> Service -> Repository -> Database -> VO` 的完整更新流程。

这一步新增或修改了什么：

- `UpdateRoleDTO.java`：新增角色编辑请求对象，只接收允许修改的字段。
- `RoleController.java`：新增 `PUT /api/roles/{code}`。
- `RoleService.java`：新增 `updateRole`，根据 code 查角色，修改字段后保存。
- `RoleControllerTest.java`：新增编辑成功、角色不存在、参数非法三个测试。
- `App.vue`：新增角色编辑状态，`saveRole` 会根据模式自动选择 POST 或 PUT。
- `RolesPage.vue`：角色表格新增“编辑”按钮，表单支持新增和编辑两种模式。
- `style.css`：新增表格行操作按钮间距和禁用输入框样式。
- `README.md`：记录第 41 步学习内容。

你需要理解：

- `PUT /api/roles/{code}`：按角色编码更新一个角色。
- `UpdateRoleDTO`：编辑接口专用 DTO，不包含 `code`。
- `@PathVariable String code`：从 URL 里读取要编辑哪个角色。
- `roleRepository.findByCode(code)`：先查到数据库里的角色实体。
- `roleRepository.save(role)`：保存修改后的角色实体。
- `editingRoleCode`：前端记录当前正在编辑哪个角色。
- `isEditingRole`：前端判断角色表单现在是新增模式还是编辑模式。
### 第 42 步：删除角色接口和前端删除按钮

目标：在角色管理表格里点击“删除”，调用后端 `DELETE /api/roles/{code}`，把数据库里的角色记录删除。

为什么要做这一步：

- 后台管理系统里的基础数据通常需要完整的增删改查能力，角色模块现在补齐“删除”动作。
- `DELETE` 是 HTTP 里表达删除资源的常见方法，路径里的 `{code}` 表示删除哪一个角色。
- 删除成功后不需要返回角色详情，所以后端返回 `ApiResponse<Void>`，最终 JSON 里的 `data` 是 `null`。
- 前端删除后要刷新角色列表，并且如果当前正在查看或编辑这条角色，需要清空详情和表单，避免页面继续显示已删除数据。

这一 新增或修改了什么：

- `RoleController.java`：新增 `DELETE /api/roles/{code}` 接口入口，删除成功返回 `data: null`。
- `RoleService.java`：新增 `deleteRole(code)`，先按 code 查询角色，存在才调用 Repository 删除。
- `RoleControllerTest.java`：新增删除成功和删除不存在角色两个测试，确认接口行为稳定。
- `App.vue`：新增 `roleDeleteLoadingCode` 和 `deleteRole(role)`，负责调用删除接口、刷新列表、清空相关状态。
- `RolesPage.vue`：角色表格新增“删除”按钮，并在删除中禁用当前行按钮。
- `README.md`：记录第 42 步的学习目标、接口和代码职责。

你需要理解：

- `@DeleteMapping("/{code}")`：匹配 `DELETE /api/roles/operator` 这种删除请求。
- `@PathVariable String code`：从 URL 路径里拿到要删除的角色编码。
- `Optional<RoleEntity>`：查询结果可能存在，也可能不存在，所以用 Optional 表达“不确定有没有”。
- `roleRepository.delete(role)`：让 JPA 删除数据库里的这一行记录。
- `ApiResponse<Void>`：表示成功响应没有业务数据，适合删除这类只需要告诉前端“成功了”的接口。
- `roleDeleteLoadingCode`：前端记录当前哪一行正在删除，避免整张表都被锁住。
- `roleDeleteLoadingCode`：前端记录当前哪一行正在删除，避免整张表都被锁住。

### 第 43 步：权限字典接口和角色页权限列表

目标：新增 `GET /api/auth/permissions`，让前端角色管理页可以看到系统里有哪些权限编码。

为什么要做这一步：

- 权限是后台系统的核心概念，角色真正有价值，是因为角色会绑定一组权限。
- 在做“角色分配权限”之前，前端需要先知道系统里有哪些权限可以选。
- 这一小步先做“权限字典查询”，不直接做勾选保存，学习节奏更稳。
- 现在权限仍然写在 Java 常量里，后面可以继续升级成数据库表。

这一 新增或修改了什么：

- `PermissionVO.java`：新增权限返回对象，包含 `code`、`name`、`description`。
- `AuthService.java`：新增 `listPermissions()`，把系统权限常量组装成前端可展示的列表。
- `AuthController.java`：新增 `GET /api/auth/permissions` 接口。
- `WebConfig.java`：把 `/api/auth/permissions` 加入登录拦截器，未登录不能访问。
- `AuthControllerTest.java`：新增权限字典接口测试，覆盖登录成功访问和未登录拦截。
- `App.vue`：新增 `permissions`、`permissionLoading`、`permissionError` 和 `loadPermissions()`。
- `RolesPage.vue`：新增“权限字典”表格，展示权限编码、名称、说明。
- `style.css`：新增权限字典区域和紧凑表格样式。

你需要理解：

- `权限编码`：例如 `user:read`，后端和前端都用它判断能不能做某个动作。
- `权限名称`：给人看的名字，例如“查看用户”。
- `权限说明`：解释这个权限具体允许做什么。
- `GET /api/auth/permissions`：查询系统有哪些权限，不修改数据。
- `PermissionVO`：返回给前端展示的权限对象，不是数据库表。
- `loadPermissions()`：前端请求权限字典，并把结果放进 `permissions`，页面再用 `v-for` 渲染。

### 第 44 步：给角色分配权限

目标：在角色管理页编辑角色时，可以勾选权限并调用 `PATCH /api/roles/{code}/permissions` 保存。

为什么要做这一步：

- 角色的核心作用是把多个权限打包起来，例如“运营管理员”可以查看用户，但不能删除用户。
- `PUT /api/roles/{code}` 已经负责编辑角色基本信息，这一步用 `PATCH` 单独更新权限，更容易理解“局部更新”。
- 这一步先把权限编码用逗号分隔字符串存在 `sys_role.permission_codes`，后面再升级成真正的角色权限关系表。
- 保存权限时后端会自动同步 `permissionCount`，前端不用手动计算权限数量。

这一 新增或修改了什么：

- `AuthPermissions.java`：新增 `listAllCodes()`，提供后端允许保存的权限编码清单。
- `RoleEntity.java`：新增 `permissionCodes` 字段，暂时用字符串保存角色拥有的权限编码。
- `RoleVO.java`：新增 `permissionCodes` 列表，方便前端用复选框回显。
- `UpdateRolePermissionsDTO.java`：新增权限分配请求对象，只接收 `permissionCodes`。
- `RoleService.java`：新增 `updateRolePermissions()`，校验权限编码、保存权限、同步权限数量。
- `RoleController.java`：新增 `PATCH /api/roles/{code}/permissions` 接口。
- `RoleControllerTest.java`：新增权限分配成功、角色不存在、权限编码非法三个测试。
- `App.vue`：新增 `saveRolePermissions()`，调用 PATCH 接口保存当前编辑角色的权限。
- `RolesPage.vue`：编辑角色时新增权限复选框，并在详情里展示角色权限编码。
- `style.css`：新增权限复选框区域样式。

你需要理解：

- `PATCH`：通常表示只修改一部分字段，这里只修改角色权限。
- `permissionCodes`：前端复选框选中的权限编码数组，例如 `["user:read", "role:manage"]`。
- `UpdateRolePermissionsDTO`：专门接收权限分配请求，避免和编辑角色名称的 DTO 混在一起。
- `validatePermissionCodes()`：后端校验前端传来的权限编码必须存在，不能相信前端。
- `joinPermissionCodes()`：把数组转成数据库里保存的字符串。
- `splitPermissionCodes()`：把数据库字符串转回前端更好用的数组。
