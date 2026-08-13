# 业务模块设计：患者 / 预约 / 基础数据

对照牙医管家截图，核心业务拆分为三块。

## 1. 登录拦截

- 请求头：`Authorization: Bearer {token}`
- 白名单：`/auth/login`、Swagger、Actuator
- Token 存 Redis，拦截器校验并写入 `SecurityContextHolder`
- 登录时同时缓存 `LoginUser`（含 clinicId、权限）

## 2. 患者管理 `biz_patient`

对应截图「患者列表 + 新增患者弹窗」：

| 能力 | 接口 |
|------|------|
| 分页列表（姓名/手机/病历号/拼音 + 医生/标签筛选） | `GET /biz/patient/list` |
| 顶部全局搜索 | `GET /biz/patient/search` |
| 详情 | `GET /biz/patient/{id}` |
| 新增/修改/删除 | `POST/PUT/DELETE /biz/patient` |
| 保存并预约 / 保存并到达 | `POST /biz/patient/saveWithAction?action=appoint\|arrive\|save` |

病历号为空时按诊所自增生成（6位）。

## 3. 预约管理 `biz_appointment`

对应「首页今日任务 + 预约日历」：

| 能力 | 接口 |
|------|------|
| 列表/今日任务 | `GET /biz/appointment/list` |
| 日历（天/周/月） | `GET /biz/appointment/calendar` |
| 新增/编辑/删除 | `POST/PUT/DELETE /biz/appointment` |
| 改状态 | `PUT /biz/appointment/status` |
| 接诊入位 | `PUT /biz/appointment/seat/{id}` |
| 今日统计卡片 | `GET /biz/appointment/stats/today` |

状态机：

```text
1已预约 → 2已确认 → 3已到达 → 4治疗中 → 5已离开
                ↘ 6已过期 / 7已流失 / 8预约未到
```

日历前端：X 轴医生列表（`/biz/basic/doctor/list`），Y 轴时间格，块数据来自 `/calendar`。

## 4. 基础数据

| 数据 | 接口前缀 |
|------|----------|
| 字典（就诊类型/预约状态/手机关系） | `/biz/basic/dict` |
| 患者标签 | `/biz/basic/tag` |
| 患者来源树 | `/biz/basic/source` |
| 诊疗项目 | `/biz/basic/item` |
| 医生列表 | `/biz/basic/doctor/list` |

## 5. 推荐前端调用顺序

1. `POST /auth/login` 取 token
2. `GET /auth/info` 取菜单权限、当前诊所
3. `GET /biz/basic/dict/appoint_status` 等拉基础下拉
4. 首页：`/biz/appointment/stats/today` + `/biz/appointment/list`（今日）
5. 患者页：`/biz/patient/list`
6. 预约页：`/biz/basic/doctor/list` + `/biz/appointment/calendar`
