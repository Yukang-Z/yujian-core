# 预约日历「医生查询」相关接口改造说明

> 文档用途：前端预约天视图已按产品交互实现（左侧诊所/医生勾选、15 分钟格子拖选新建）。  
> 请后端按本文改造/补充接口；**优先改现有接口**，不必整套重写。  
> 编写日期：2026-08-20  
> 关联前端：`yujian-admin-web` → `src/views/appointment/calendar/index.vue`

---

## 1. 背景与场景

### 1.1 产品交互（前端已实现）

1. 预约页日历下方有 Tab：**预约查询** / **医生查询**。
2. **医生查询**：
   - 可选择诊所；
   - 展示该诊所下医生列表；
   - 医生名称后展示**日历所选日期**的预约患者数（如 `张翰 [5]`）；
   - 勾选后，右侧按医生分列展示。
3. 右侧时间轴为 **15 分钟**一格；可拖选格子打开「新建预约」并预填医生、起止时间。

### 1.2 当前后端现状

| 接口 | 现状 | 问题 |
|------|------|------|
| `GET /biz/basic/doctor/list` | 已有；注释写明 `clinicId` 忽略 | 多诊所下无法按所选诊所列医生 |
| `GET /biz/appointment/dayGrid` | 已有；`clinicId` 忽略 | 选其他诊所后网格/人数仍是会话诊所 |
| `POST /biz/appointment` | 已有 | 当前诊所内够用 |
| `GET /biz/appointment/stats/statusCount` | 已有 | 建议与 dayGrid 诊所范围一致 |

根因：`SecurityContextHolder.requireClinicId(requestClinicId)` **一律使用会话当前诊所，忽略请求入参**。  
单诊所或只看当前会话诊所时前端可用；**跨授权诊所筛选时不够**。

### 1.3 改造目标（一句话）

在账号**有权限**的前提下，请求中的 `clinicId` 必须生效；空则回退当前会话诊所。

---

## 2. 改造清单与优先级

| 优先级 | 项 | 类型 | 是否必须 |
|--------|----|------|----------|
| P0 | `GET /biz/basic/doctor/list` 支持授权 `clinicId` | 改现有 | **必须** |
| P0 | `GET /biz/appointment/dayGrid` 支持授权 `clinicId` | 改现有 | **必须** |
| P1 | `doctor/list` 支持 `keyword` | 改现有 | 建议 |
| P1 | `dayGrid` 支持 `doctorIds` | 改现有 | 建议 |
| P2 | `GET /biz/appointment/doctorDayCount` | 新建（可选） | 否，前端已用 dayGrid.count |
| — | `POST /biz/appointment` 等 | 保持 | 无需新开 |

---

## 3. P0：医生列表（改现有）

### 3.1 基本信息

- **Method / Path**：`GET /biz/basic/doctor/list`
- **原因**：左侧「医生查询」先选诊所再列医生；现忽略 `clinicId`，多诊所场景无法对齐产品。
- **场景**：预约页 → 医生查询 → 下拉选「XX 分院」→ 列出该院医生 → 勾选后右侧分列。

### 3.2 入参（Query）

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| clinicId | Long | 否 | 要查询的诊所 ID；空 = 当前会话诊所 |
| keyword | String | 否 | 姓名 / 拼音 / 手机号模糊匹配（P1，建议一并做） |

### 3.3 反参

统一响应：`{ code, msg, data }`，`code = 200` 成功。

```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 12,
      "name": "张翰",
      "empNo": "D001",
      "position": "医生",
      "clinicId": 10,
      "mobile": "13800138000"
    }
  ]
}
```

字段说明：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 医生（员工）ID |
| name | String | 姓名 |
| empNo | String | 工号，可空 |
| position | String | 职位 |
| clinicId | Long | 所属/关联诊所 |
| mobile | String | 手机（可选，便于 keyword 搜索；可脱敏） |

### 3.4 接口逻辑

1. 解析诊所：
   - `clinicId` 为空 → 使用会话当前诊所；未选诊所则报错（与现网一致）。
   - `clinicId` 有值 → **校验当前登录账号是否有权进入该诊所**；无权返回业务错误（如 403 / 业务码）。
2. 查询该诊所关联、在职（`employStatus=1`）、启用（`status=0`）、职位含「医生/医师」等条件的员工（与现有 `selectDoctorList` 规则对齐，可沿用）。
3. 若传入 `keyword`，按 `name` / 拼音 / `mobile` 模糊过滤。
4. 按排序字段升序返回列表。
5. **不要**再无条件忽略请求中的 `clinicId`。

### 3.5 权限与安全

- 仅允许查询账号 `clinics`（可进入诊所列表）中的诊所。
- 禁止通过随意传 `clinicId` 越权查看其他诊所医生。

---

## 4. P0：预约天视图（改现有）

### 4.1 基本信息

- **Method / Path**：`GET /biz/appointment/dayGrid`
- **原因**：右侧分列、左侧医生当日人数均依赖本接口；选其他诊所后若仍按会话诊所出数，列表与网格会错位。
- **场景**：日历选日期 +（可选）选诊所 → 右侧按医生分列；左侧 `医生名 [N]` 使用 `columns[].count`。

### 4.2 入参（Query）

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| day | String | **是** | 日期，格式 `yyyy-MM-dd` |
| clinicId | Long | 否 | 授权诊所；空 = 当前会话诊所（**必须真正生效**） |
| status | String | 否 | 预约状态，多个逗号分隔，如 `1,2,3` |
| doctorIds | String | 否 | 医生 ID 逗号分隔，如 `0,12,15`；`0` 表示未指定医生；空 = 返回全部列（P1） |

### 4.3 反参

```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "day": "2026-08-20",
    "total": 12,
    "columns": [
      {
        "doctorId": 0,
        "doctorName": "未指定医生",
        "count": 0,
        "appointments": []
      },
      {
        "doctorId": 12,
        "doctorName": "张翰",
        "count": 5,
        "appointments": [
          {
            "id": 1001,
            "patientId": 2001,
            "patientName": "王小明",
            "medicalRecordNo": "YJ20260001",
            "mobile": "138****0000",
            "doctorId": 12,
            "doctorName": "张翰",
            "startTime": "2026-08-20 09:00:00",
            "endTime": "2026-08-20 09:45:00",
            "status": 1,
            "visitType": 2,
            "itemId": 3,
            "itemName": "正畸取模",
            "itemColor": "#67C23A",
            "remark": ""
          }
        ]
      }
    ]
  }
}
```

`data` 字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| day | String / Date | 查询日（建议序列化为 `yyyy-MM-dd`） |
| total | Integer | 当日符合条件的预约总数（或返回列内合计，前后端约定一种并保持稳定） |
| columns | Array | 医生列 |
| columns[].doctorId | Long | `0` = 未指定医生 |
| columns[].doctorName | String | 列标题 |
| columns[].count | Integer | **该日该列预约条数**（与 status 筛选一致） |
| columns[].appointments | Array | 该列预约明细 |

预约对象字段保持与现有日历/详情一致即可（至少含：`id, patientId, patientName, doctorId, startTime, endTime, status, visitType, itemId, itemName, itemColor, remark`）。

### 4.4 接口逻辑

1. 校验并解析 `clinicId`（规则同 §3.4，禁止越权）。
2. 将 `day` 转为当日 `[00:00:00, 次日 00:00:00)` 时间范围。
3. 查询该诊所下该时间范围预约；若传 `status`，按状态过滤。
4. 构建列：
   - **固定**第一列或固定包含：`doctorId=0`，名称「未指定医生」（与现网文案保持一致即可）；
   - 其余列为该诊所医生（规则同医生列表）。
5. 预约按 `doctorId` 归列；`doctorId == null` 归入 `0`。
6. 每列 `count = appointments.size()`（过滤后）。
7. 若传 `doctorIds`，只返回 ID 落在集合内的列（含 `0`）。
8. 返回 `day / columns / total`。

### 4.5 与前端约定

- 前端勾选医生目前以**客户端过滤列**为主；即使暂未实现 `doctorIds`，只要 `clinicId` 生效即可联调多诊所。
- 左侧人数直接读 `columns[].count`，无需另开接口也能工作。

---

## 5. P2（可选）：医生当日预约数

> 前端已用 `dayGrid` 的 `count`，**可不实现**。弱网或左侧不想拉整列预约时可再加。

### 5.1 基本信息

- **Method / Path**：`GET /biz/appointment/doctorDayCount`
- **原因**：仅刷新左侧人数时比完整 `dayGrid` 更轻。
- **场景**：医生查询 Tab 快速展示 `[N]`。

### 5.2 入参（Query）

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| day | String | 是 | yyyy-MM-dd |
| clinicId | Long | 否 | 授权诊所；空 = 当前会话 |
| status | String | 否 | 同 dayGrid |

### 5.3 反参

```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    { "doctorId": 0, "doctorName": "未指定医生", "count": 0 },
    { "doctorId": 12, "doctorName": "张翰", "count": 5 }
  ]
}
```

### 5.4 接口逻辑

1. 校验 `clinicId`（同前）。
2. 按诊所 + 自然日（+ 可选 status）对预约 `GROUP BY doctor_id` 计数；`null` 记为 `0`。
3. 补全该诊所无预约的医生，`count=0`。
4. 始终包含未指定医生一行。

---

## 6. 已有接口（确认即可，无需新开）

### 6.1 预约状态计数

- **Path**：`GET /biz/appointment/stats/statusCount`
- **场景**：左侧「预约查询」各状态旁的数量。
- **建议**：若 dayGrid 支持授权 `clinicId`，本接口同样支持并校验，保证状态数与网格同一诊所。

入参（现有 + 建议）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| beginTime | String | 否 | yyyy-MM-dd HH:mm:ss |
| endTime | String | 否 | yyyy-MM-dd HH:mm:ss |
| doctorId | Long | 否 | 按医生过滤 |
| clinicId | Long | 否 | **建议生效**，规则同 dayGrid |

### 6.2 新建预约

- **Path**：`POST /biz/appointment`
- **场景**：拖选时间格 / 点击「新建预约」。
- **说明**：当前会话诊所写入即可；前端会传起止时间（15 分钟粒度）。

请求体示例：

```json
{
  "patientId": 2001,
  "doctorId": 12,
  "startTime": "2026-08-20 09:00:00",
  "endTime": "2026-08-20 09:45:00",
  "visitType": 2,
  "itemId": 3,
  "itemName": "正畸取模",
  "remark": ""
}
```

| 字段 | 说明 |
|------|------|
| patientId | 必填 |
| doctorId | 可空或 `0`/`null` 表示未指定医生 |
| startTime / endTime | 必填，`endTime > startTime` |
| visitType | 1 初诊 / 2 复诊 |
| itemId / itemName | 可选 |
| remark | 可选 |
| clinicId | 可不传，后端用会话诊所 |

反参：成功 `code=200` 即可；若能返回新建 `id` 更佳（非必须）。

### 6.3 患者搜索、诊疗项目

- 患者搜索：沿用现有患者 keyword 搜索接口（新建弹窗远程搜索）。
- `GET /biz/basic/item/list`：就诊项目；若有 `duration`（分钟）字段，前端可用其推算结束时间（可选增强）。

---

## 7. 诊所解析建议（实现要点）

将「一律忽略 requestClinicId」改为类似逻辑（伪代码）：

```text
function resolveClinicId(requestClinicId):
  current = session.clinicId
  if current is null:
    throw "请先选择诊所后再操作"

  if requestClinicId is null:
    return current

  if requestClinicId not in session.authorizedClinicIds:
    throw "无权访问该诊所"

  return requestClinicId
```

- **仅**对需要「按所选诊所查看」的查询接口启用上述解析（至少：`doctor/list`、`dayGrid`；建议含 `statusCount`）。
- 写操作（新建预约等）仍建议强制使用**会话当前诊所**，避免误写到未切换的诊所；若产品要求「在筛选诊所下直接建预约」，需产品确认后再放开写路径的 `clinicId`。

---

## 8. 联调验收清单

- [ ] 会话诊所 A：不传 `clinicId`，`doctor/list` / `dayGrid` 结果与现网一致。
- [ ] 账号同时有诊所 A、B：传 `clinicId=B`，医生列表为 B 的医生。
- [ ] 同上，`dayGrid?day=当天&clinicId=B` 列与人数属于 B。
- [ ] 传无权 `clinicId` → 业务错误，无数据泄露。
- [ ] `dayGrid` 含 `doctorId=0` 未指定列；`count` 与列内预约数一致。
- [ ] （若做）`keyword`、`doctorIds` 行为符合 §3 / §4。
- [ ] 前端：选诊所 → 勾选医生 → 右侧列变化；拖格新建预约成功。

---

## 9. 前端已对接说明（供后端对照）

前端会传（后端未支持时会被忽略，支持后自动生效）：

| 调用 | 参数 |
|------|------|
| `GET /biz/basic/doctor/list` | `clinicId?` |
| `GET /biz/appointment/dayGrid` | `day`、`status?`、`clinicId?`、（预留）`doctorIds?` |
| `POST /biz/appointment` | 见 §6.2 |

前端仓库 API 封装：

- `src/api/system.ts` → `fetchDoctorList`
- `src/api/appointment.ts` → `fetchAppointmentDayGrid` / `createAppointment`

---

## 10. 修订记录

| 日期 | 说明 |
|------|------|
| 2026-08-20 | 初稿：医生查询多诊所 + dayGrid 对齐说明 |
