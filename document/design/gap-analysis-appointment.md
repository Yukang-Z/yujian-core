# 截图对照：查漏补缺 & 预约闭环

> 对照牙医管家：天/周/月/列表/回收站 + 患者 + 系统管理。  
> 目标：登录 → 患者 → 预约 → 日历/状态流转 → 回收站 可整套跑通。

## 一、已具备（可串通主链路）

| 能力 | 状态 |
|------|------|
| 登录鉴权 / Token 拦截 | ✅ |
| 诊所 / 员工 / 角色 / 菜单 | ✅ |
| 患者 CRUD + 搜索 + 标签/来源 | ✅ |
| 预约 CRUD + 日历 + 状态流转 | ✅ |
| 基础字典 / 诊疗项目 / 医生列 | ✅ |
| Nacos 注册+配置 | ✅ |

## 二、本次对照截图补齐

| 截图能力 | 缺口 | 本次方案 |
|----------|------|----------|
| 左侧状态计数 全部/已预约/... | 缺区间计数 | `GET /biz/appointment/stats/statusCount` |
| 天视图按医生分列 | 仅扁平 calendar | `GET /biz/appointment/dayGrid` |
| 医生时间冲突 | 无校验 | 新增/修改时冲突检测 |
| 列表「确认/取消」 | 仅通用 status | `/confirm/{id}`、`/cancel` |
| 操作「日志」 | 无 | 表 `t_appointment_log` + `GET /{id}/logs` |
| 回收站还原/彻底删除 | 仅逻辑删 | `/recycle/list` `/restore` 彻底删 |
| 取消原因 | 无字段 | `cancel_reason` + 字典 `cancel_reason` |
| 预约类型/来源/网络预约 | 无 | `appoint_type` / `appoint_source` |
| 日历色块 | 无 | `item_color`（项目表+预约冗余） |
| 新增日程 | 无 | 表 `t_schedule` + `/biz/schedule` |

SQL：`document/sql/00_full_schema.sql` + `document/sql/01_init_data.sql`

## 三、推荐前端调用闭环

```text
1. POST /auth/login
2. GET  /auth/info
3. GET  /biz/basic/dict/appoint_status|visit_type|cancel_reason
4. GET  /biz/basic/doctor/list
5. GET  /biz/basic/item/list
6. POST /biz/patient  或  /biz/patient/saveWithAction?action=appoint
7. POST /biz/appointment          （冲突自动校验）
8. GET  /biz/appointment/dayGrid?day=yyyy-MM-dd
   GET  /biz/appointment/calendar （周/月）
   GET  /biz/appointment/list     （列表）
9. GET  /biz/appointment/stats/statusCount
10. PUT /biz/appointment/confirm/{id}
    PUT /biz/appointment/status   （到达/治疗/离开）
    PUT /biz/appointment/seat/{id}
11. PUT /biz/appointment/cancel   或 DELETE /biz/appointment/{id}
12. GET /biz/appointment/recycle/list → restore / permanent delete
```

## 四、仍未做（下一批，非预约主链路阻断项）

| 模块 | 说明 |
|------|------|
| 回访 | 独立表 + 任务列表（截图侧栏有「回访」） |
| 收费/结算 | 账单、收款；患者仅有 owe/paid 字段 |
| 电子病历 / 影像 | AI 影像、口扫 |
| 库房 | 耗材出入库 |
| 营销/商城 | 非门诊主流程 |
| 网络预约独立队列 | 可用 `appoint_source=online` 先筛，后续可拆表 |
| 过期自动任务 | 可用 yujian-task 定时把超时「已预约」置为「已过期」 |
| 导出 Excel | 列表导出 |
| 数据权限过滤 | role.data_scope 尚未落到 SQL |

## 五、表清单（业务闭环相关）

**已有：** `t_clinic` / `t_dept` / `t_employee` / `t_role` / `t_menu` 等系统表 + `t_patient` / `t_appointment` / `t_dict_*` / `t_patient_tag*` / `t_patient_source` / `t_treatment_item`

**本次新增：**  
- `t_appointment` 增补字段  
- `t_appointment_log`  
- `t_schedule`  
- `t_treatment_item.item_color`
