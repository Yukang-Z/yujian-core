# 预约列表 / 回收站 / 日志 / 修改 — 后端接口缺口

> 日期：2026-08-20  
> 产品范围：顶栏仅 **天 / 列表 / 回收站**（周、月不做）  
> 列表需：左侧快捷筛选、多条件查询、操作日志、修改预约  
> 联调主文档：`document/api/frontend-api.md`

---

## 1. 结论

| 模块 | 结论 |
|------|------|
| 天视图 | ✅ 已有 |
| 列表基础分页 | ⚠️ 部分够用，筛选项缺口见下 |
| 回收站 | ⚠️ 有 list/restore/remove，缺清空全部 |
| 操作日志 | ✅ `GET /{id}/logs` 字段基本够展示 |
| 修改预约 | ✅ `GET /{id}` + `POST /edit` 基本够用（含 itemIds） |

**必须补齐（P0）** 才能对齐截图查询条件与左侧「待确定」。

---

## 2. 列表查询 `GET /biz/appointment/list`（改现有）

### 2.1 现有已支持

| 参数 | 说明 |
|------|------|
| keyword | 患者姓名/手机/病历号 |
| doctorId | 预约医生 |
| consultantId | 咨询师 |
| visitType | 1/2/3 |
| status | **单个**状态码 |
| appointSource | 预约来源 |
| beginTime / endTime | 按 **预约开始时间** `start_time` 区间 |
| pageNum / pageSize | 分页 |

`clinicId` 入参存在但 **当前被忽略**（强制会话诊所）。

### 2.2 缺少 / 需改造

| 优先级 | 参数 | 说明 | 场景 |
|--------|------|------|------|
| P0 | `clinicId` | 授权范围内生效（同 dayGrid） | 查询条件「门诊」 |
| P0 | `createBeginTime` / `createEndTime` | 按 `create_time` 过滤 | 查询条件「创建时间」 |
| P0 | `appointType` | 如 `normal` / `walkin` / `online` / **`pending`** | 预约类型；左侧「待确定」 |
| P1 | `status` 多值 | 逗号分隔 `1,2,6` 或重复传参 | 状态下拉多选 |
| P1 | 反参 `clinicName` | 列表展示门诊名 | 表格列 |
| P2 | 反参 `items` / `itemIds` | 与详情一致 | 预约项目列完整展示 |

### 2.3 左侧快捷筛选约定（需后端确认）

| 左侧项 | 建议实现 |
|--------|----------|
| 所有预约 | 不传特殊筛选（或排除回收站，现网 `is_delete=0` 已满足） |
| 待确定预约 | **现状无状态码**。建议 `appointType=pending`，或新增状态；请后端定稿 |
| 已过期预约 | `status=6` |
| 已流失预约 | `status=7` |

请后端在文档中写死「待确定」字段与取值，前端按此对接。

**定稿（已落地）**：待确定 = `appointType=pending`（字典 `appoint_type`），**不是**新 status。左侧「待确定预约」传 `appointType=pending`。

### 2.4 建议反参字段（列表行）

已有：`patientName/status/medicalRecordNo/gender/age/mobile/consultantName/createTime/startTime/endTime/doctorName/appointType/appointSource/visitType/itemName/creatorName/cancelReason`  

建议补：`clinicName`、`items[]`。

---

## 3. 回收站

### 3.1 已有

| 接口 | 说明 |
|------|------|
| `GET /biz/appointment/recycle/list` | keyword / doctorId / consultantId / beginTime / endTime / page |
| `POST /biz/appointment/recycle/restore/{id}` | 还原 |
| `POST /biz/appointment/recycle/remove/{id}` | 彻底删除 |

### 3.2 缺口

| 优先级 | 项 | 说明 |
|--------|----|------|
| P1 | `clinicId` 授权生效 | 与列表一致 |
| P1 | 清空回收站 | 截图「清空预约」：如 `POST /biz/appointment/recycle/clear`（可带时间范围） |
| P2 | 反参确保含 `cancelReason`、`creatorName` | 表结构已有，确认 SELECT 带回 |

---

## 4. 操作日志 — 已够用

`GET /biz/appointment/{id}/logs`

反参建议（现实体已基本具备）：

```json
[
  {
    "id": 1,
    "appointmentId": 1001,
    "action": "update",
    "beforeStatus": 1,
    "afterStatus": 2,
    "content": "修改预约状态（已预约 → 已确认）",
    "operatorName": "管理员",
    "createTime": "2026-08-20 14:00:00"
  }
]
```

**建议（P2）**：改时间/改医生时，`content` 写清变更前后值（截图文案样式）；前端可直接展示 `content` + 操作人 + 时间。

---

## 5. 修改预约 — 基本够用

| 接口 | 说明 |
|------|------|
| `GET /biz/appointment/{id}` | 详情含 itemIds/items |
| `POST /biz/appointment/edit` | Body 同新建（含 itemIds、clinicId 规则请与新建对齐） |

### 5.1 建议确认

| 项 | 说明 |
|----|------|
| edit 的 `clinicId` | 是否允许改到授权内其它门诊（与新建一致更佳） |
| 改期冲突校验 | 是否与新建相同 `checkConflict` |
| 日期 ≥ 今天 | 改到过去是否拦截（产品定） |

---

## 6. 预约类型 / 来源字典

| 字段 | 现状（代码注释） | 产品截图 |
|------|------------------|----------|
| appointType | normal / walkin / online | 普通预约、到店预约、**待确定** |
| appointSource | clinic / online / wechat | 普通预约、到店预约等文案 |

请提供：

1. 完整枚举值表（码 → 中文）  
2. 是否走字典 `appoint_type` / `appoint_source`（`GET /biz/basic/dict/{type}`）  
3. 「待确定」落在 type 还是 status  

---

## 7. 前端将对接策略（后端未改完时）

| 能力 | 策略 |
|------|------|
| 列表已有条件 | 直接调 list |
| 门诊 / 创建时间 / 类型 | UI 先做；接口未支持前加注释，联调后传参 |
| 待确定 | 左侧入口保留；无约定前提示「待后端定稿」或暂不请求 |
| 日志 / 修改 | 用现有 logs + detail + edit |
| 回收站 | list/restore/remove；清空等接口 |
| 周 / 月 | **不做**，顶栏仅天/列表/回收站 |

---

## 8. 修订记录

| 日期 | 说明 |
|------|------|
| 2026-08-20 | 初稿：列表/回收站/日志/修改缺口 |
| 2026-08-20 | **已落地**：P0+P1；待确定=`appointType=pending`；见 `frontend-api.md` §8.1/8.5/8.6 |
