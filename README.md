# 宇健口腔医疗系统（yujian-core）

基于 **JDK 8 + Spring Boot 2.3 + Spring Cloud Hoxton** 的口腔医疗微服务架构，集成 **MySQL**、**Redis**、**Eureka（注册中心）**、**Gateway**、**XXL-JOB**。

## 一、模块说明

| 模块 | 说明 | 默认端口 |
|------|------|----------|
| `document` | 项目文档、SQL 脚本 | - |
| `yujian-common` | 公共实体、工具、统一响应、Redis/MyBatis 配置 | - |
| `yujian-eureka` | Eureka 注册中心 | 8761 |
| `yujian-gateway` | API 网关 | 8080 |
| `yujian-admin` | Web 端核心业务（系统管理等） | 8081 |
| `yujian-api` | 客户端（移动端）核心接口 | 8082 |
| `yujian-listener` | 消息监听服务（RabbitMQ） | 8083 |
| `yujian-task` | 定时任务执行器（XXL-JOB Executor） | 8084 |
| `yujian-xxl` | 调度中心接入说明模块（建议独立部署官方 admin） | 8086 |

包名统一：`com.yujian.*`  
配置：`application.yml`（公共）+ `application-{test|uat|prod}.yml`（环境）

## 二、技术栈

- JDK 8
- Spring Boot 2.3.12.RELEASE
- Spring Cloud Hoxton.SR12
- Eureka（Netflix）注册中心
- MyBatis-Plus 3.4.3
- Druid 连接池
- MySQL 5.7+/8.x
- Redis
- Gateway
- XXL-JOB 2.3.1
- Knife4j（接口文档）
- Hutool / Lombok / Fastjson / Sa-Token

## 三、环境准备

1. 安装 JDK 8、Maven 3.6+
2. 启动 MySQL，执行：
   - `document/sql/00_full_schema.sql`
   - `document/sql/01_init_data.sql`
3. 启动 Redis（默认 `127.0.0.1:6379`）
4. （可选）RabbitMQ：监听服务需要
5. （可选）部署官方 [xxl-job-admin](https://github.com/xuxueli/xxl-job)，默认 `http://127.0.0.1:8085/xxl-job-admin`

环境配置在各服务 `application-test.yml` / `application-uat.yml` / `application-prod.yml`，默认 `spring.profiles.active=test`。  
切换环境：`-Dspring.profiles.active=uat` 或 `prod`。

## 四、启动顺序

```text
1. MySQL / Redis
2. yujian-eureka（8761）
3. yujian-admin（8081）
4. yujian-gateway（8080，按需）
5. yujian-api / listener / task / xxl（按需）
```

本地编译：

```bash
mvn clean install -DskipTests
```

网关转发示例：

- Admin：`http://localhost:8080/admin/system/clinic/list`
- API：`http://localhost:8080/api/app/health`

也可直连：`http://localhost:8081/system/clinic/list`

接口文档（Admin）：`http://localhost:8081/doc.html`  
前端联调说明：`document/api/frontend-api.md`

## 五、系统管理功能

已实现与口腔诊所场景对应的系统管理后端接口：

### 5.1 表结构

| 表名 | 说明 |
|------|------|
| `t_clinic` | 诊所管理（支持总分院 parent_id） |
| `t_dept` | 部门 |
| `t_employee` | 员工（工号、岗位、在职状态、手机关联等） |
| `t_role` | 角色（含数据权限 data_scope） |
| `t_menu` | 菜单/权限树（web / mobile） |
| `t_employee_role` | 员工-角色 |
| `t_role_menu` | 角色-菜单 |

### 5.2 登录拦截

- Sa-Token + Redis 会话；Header：`Authorization: Bearer {token}`
- 白名单：`/auth/login`、Knife4j、Actuator
- 当前用户：`SecurityContextHolder.getLoginUser()`（由 Sa-Token Session 同步）

登录：`POST /auth/login`  
当前用户：`GET /auth/info`  
退出：`POST /auth/logout`

### 5.3 系统管理接口

#### 诊所管理 `/system/clinic`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 诊所列表 |
| GET | `/tree` | 诊所树数据 |
| GET | `/{id}` | 详情 |
| POST | `/` | 新增 |
| PUT | `/` | 修改 |
| DELETE | `/{id}` | 删除 |

#### 部门管理 `/system/dept`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 部门列表 |
| GET | `/{id}` | 详情 |
| POST | `/` | 新增 |
| PUT | `/` | 修改 |
| DELETE | `/{id}` | 删除 |

#### 员工管理 `/system/employee`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 分页列表（含 roleIds） |
| GET | `/{id}` | 详情（含 roleIds） |
| POST | `/` | 新增（含角色绑定） |
| PUT | `/` | 修改（roleIds 为 null 不改角色；密码勿传） |
| PUT | `/status` | 仅启停 `{ id, status }` |
| DELETE | `/{id}` | 删除 |
| PUT | `/resetPwd` | 重置密码 |
| PUT | `/sort/{id}/{direction}` | 上移/下移（up/down） |

#### 角色设置 `/system/role`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 角色列表 |
| GET | `/{id}` | 详情（含 menuIds） |
| POST | `/` | 新增 |
| PUT | `/` | 修改 |
| DELETE | `/{id}` | 删除 |
| PUT | `/auth` | 保存角色菜单权限 |
| GET | `/{id}/menus` | 角色已选菜单 |
| PUT | `/move/{id}/{direction}` | 上移/下移 |

#### 权限管理 `/system/menu`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 菜单列表 |
| GET | `/tree?platform=web` | 权限树（web/mobile） |
| GET | `/{id}` | 详情 |
| POST | `/` | 新增 |
| PUT | `/` | 修改 |
| DELETE | `/{id}` | 删除 |
| GET | `/employee/{id}` | 员工菜单树 |
| GET | `/employee/{id}/perms` | 员工权限标识 |

### 5.4 默认账号

- 用户名：`admin`
- 密码：`123456`
- 角色：管理员（拥有全部菜单权限）

## 六、核心业务（患者 / 预约 / 基础数据）

对照牙医管家截图实现，详细设计见 `document/design/biz-patient-appointment.md`。

初始化 SQL：
- `document/sql/00_full_schema.sql`
- `document/sql/01_init_data.sql`

### 6.1 患者管理 `/biz/patient`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 分页（keyword/doctorId/tagId） |
| GET | `/search` | 全局搜索姓名/手机/病历号/拼音 |
| GET | `/{id}` | 详情 |
| POST | `/` | 新增（病历号可自动生成） |
| PUT | `/` | 修改 |
| DELETE | `/{id}` | 删除 |
| POST | `/saveWithAction?action=` | save / appoint / arrive |

### 6.2 预约管理 `/biz/appointment`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 列表/今日任务 |
| GET | `/calendar` | 日历视图（天周月） |
| GET | `/{id}` | 详情 |
| POST | `/` | 新增预约 |
| PUT | `/` | 修改 |
| DELETE | `/{id}` | 删除 |
| PUT | `/status` | 改状态 |
| PUT | `/seat/{id}` | 接诊入位 |
| GET | `/stats/today` | 首页今日统计卡片 |

预约状态：1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到

### 6.3 基础数据 `/biz/basic`

| 路径 | 说明 |
|------|------|
| `/dict/{dictType}` | 字典（visit_type / appoint_status 等） |
| `/tag/list` | 患者标签 |
| `/source/tree` | 患者来源树 |
| `/item/list` | 诊疗项目 |
| `/doctor/list` | 日历医生列 |

## 七、工程结构

```text
yujian-core
├── document/                 # 文档与 SQL
├── yujian-common/            # 公共模块
├── yujian-eureka/            # Eureka 注册中心
├── yujian-gateway/           # 网关
├── yujian-admin/             # Web 业务
├── yujian-api/               # 客户端接口
├── yujian-listener/          # 消息监听
├── yujian-task/              # 定时任务
├── yujian-xxl/               # 调度中心接入
├── pom.xml
└── README.md
```

## 八、后续扩展建议

1. Gateway 统一 Token 校验与限流
2. 数据权限（按诊所/部门/本人过滤）
3. 收费结算、回访、电子病历
4. 操作日志、登录日志
5. 按需引入配置中心（如继续用 yml 多环境即可）

## 九、许可证

仅供宇健口腔项目内部使用。
